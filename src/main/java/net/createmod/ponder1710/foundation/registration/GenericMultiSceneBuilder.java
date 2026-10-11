package net.createmod.ponder1710.foundation.registration;

import java.util.function.Consumer;

import net.createmod.ponder1710.api.registration.MultiSceneBuilder;
import net.createmod.ponder1710.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder1710.api.registration.StoryBoardEntry;
import net.createmod.ponder1710.api.scene.PonderStoryBoard;
import net.minecraft.util.ResourceLocation;

public class GenericMultiSceneBuilder<T> implements MultiSceneBuilder {

	protected Iterable<? extends T> components;
	protected PonderSceneRegistrationHelper<T> helper;

	protected GenericMultiSceneBuilder(PonderSceneRegistrationHelper<T> helper, Iterable<? extends T> components) {
		this.helper = helper;
		this.components = components;
	}

	@Override
	public MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
										   PonderStoryBoard storyBoard) {
		return addStoryBoard(schematicLocation, storyBoard, $ -> {
		});
	}

	@Override
	public MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
										   PonderStoryBoard storyBoard,
										   ResourceLocation... tags) {
		return addStoryBoard(schematicLocation, storyBoard, sb -> sb.highlightTags(tags));
	}

	@Override
	public MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
										   PonderStoryBoard storyBoard,
										   Consumer<StoryBoardEntry> extras) {
		components.forEach(c -> extras.accept(helper.addStoryBoard(c, schematicLocation, storyBoard)));
		return this;
	}

	@Override
	public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard) {
		return addStoryBoard(helper.asLocation(schematicPath), storyBoard);
	}

	@Override
	public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard,
										   ResourceLocation... tags) {
		return addStoryBoard(helper.asLocation(schematicPath), storyBoard, tags);
	}

	@Override
	public MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard,
										   Consumer<StoryBoardEntry> extras) {
		return addStoryBoard(helper.asLocation(schematicPath), storyBoard, extras);
	}

}
