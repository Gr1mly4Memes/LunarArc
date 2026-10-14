package io.papermc.paper.command.brigadier.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.adventure.PaperAdventure;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Paper builds the Adventure view with a class patched into PlayerChatMessage; LunarArc leaves vanilla's record alone and
 * wraps the resolved message here instead.
 */
@NullMarked
public record SignedMessageResolverImpl(MessageArgument.Message message) implements SignedMessageResolver {

    @Override
    public String content() {
        return this.message.text();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public CompletableFuture<SignedMessage> resolveSignedMessage(final String argumentName, final CommandContext erased) throws CommandSyntaxException {
        final CompletableFuture<SignedMessage> future = new CompletableFuture<>();

        final MessageArgument.Message response = ((CommandContext<CommandSourceStack>) erased).getArgument(argumentName, SignedMessageResolverImpl.class).message;
        final CommandSourceStack source = ((CommandContext<CommandSourceStack>) erased).getSource();
        final java.util.function.Consumer<PlayerChatMessage> callback = message -> future.complete(adventureView(message));
        final net.minecraft.network.chat.Component component = response.resolveComponent(source);
        final PlayerChatMessage signed = source.getSigningContext().getArgument(argumentName);
        if (signed != null) {
            MessageArgument.resolveSignedMessage(callback, source, signed.withUnsignedContent(component));
        } else {
            MessageArgument.resolveDisguisedMessage(callback, source, PlayerChatMessage.system(response.text()).withUnsignedContent(component));
        }
        return future;
    }

    private static SignedMessage adventureView(final PlayerChatMessage message) {
        return new SignedMessage() {
            @Override
            public Instant timestamp() {
                return message.timeStamp();
            }

            @Override
            public long salt() {
                return message.salt();
            }

            @Override
            public @Nullable Signature signature() {
                return message.signature() == null ? null : SignedMessage.signature(message.signature().bytes());
            }

            @Override
            public net.kyori.adventure.text.@Nullable Component unsignedContent() {
                return message.unsignedContent() == null ? null : PaperAdventure.asAdventure(message.unsignedContent());
            }

            @Override
            public String message() {
                return message.signedContent();
            }

            @Override
            public Identity identity() {
                return Identity.identity(message.sender());
            }
        };
    }
}
