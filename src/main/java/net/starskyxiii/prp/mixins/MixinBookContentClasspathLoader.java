package net.starskyxiii.prp.mixins;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import net.starskyxiii.prp.PRP;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.BookContentClasspathLoader;
import vazkii.patchouli.client.book.BookContentLoader;
import vazkii.patchouli.common.book.Book;

@Mixin(
		value = {BookContentClasspathLoader.class},
		remap = false
)
public class MixinBookContentClasspathLoader {
	public MixinBookContentClasspathLoader() {
	}

	@Inject(
			at = {@At("HEAD")},
			method = {"findFiles"},
			cancellable = true
	)
	private void findFiles(Book book, String dir, List<Identifier> list, CallbackInfo ci) {
		String prefix = String.format("%s/%s/%s/%s", "patchouli_books", book.id.getPath(), "en_us", dir);
		Collection<Identifier> files = MinecraftClient.getInstance().getResourceManager().findResources(prefix, (p) -> p.endsWith(".json"));
		Stream<Identifier> var10000 = files.stream().distinct().filter((file) -> file.getNamespace().equals(book.id.getNamespace())).map((file) -> {
			Preconditions.checkArgument(file.getPath().startsWith(prefix));
			Preconditions.checkArgument(file.getPath().endsWith(".json"));
			String newPath = file.getPath().substring(prefix.length(), file.getPath().length() - ".json".length());
			if (newPath.startsWith("/")) {
				newPath = newPath.substring(1);
			}

			return new Identifier(file.getNamespace(), newPath);
		});
		Objects.requireNonNull(list);
		var10000.forEach(list::add);
		if (!list.isEmpty()) {
			ci.cancel();
		}

	}

	@Inject(
			at = {@At("HEAD")},
			method = {"loadJson"},
			cancellable = true,
			remap = false
	)
	private void loadJson(Book book, Identifier resloc, @Nullable Identifier fallback, CallbackInfoReturnable<JsonElement> callback) {
		PRP.LOGGER.debug("Loading {}", resloc);
		ResourceManager manager = MinecraftClient.getInstance().getResourceManager();

		try {
			if (manager.containsResource(resloc)) {
				callback.setReturnValue(BookContentLoader.streamToJson(manager.getResource(resloc).getInputStream()));
			} else if (fallback != null && manager.containsResource(fallback)) {
				callback.setReturnValue(BookContentLoader.streamToJson(manager.getResource(fallback).getInputStream()));
			}

		} catch (IOException var7) {
			throw new UncheckedIOException(var7);
		}
	}
}