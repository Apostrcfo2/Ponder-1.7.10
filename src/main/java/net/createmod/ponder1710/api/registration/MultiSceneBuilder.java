package net.createmod.ponder1710.api.registration;

import java.util.function.Consumer;

import net.createmod.ponder1710.api.scene.PonderStoryBoard;
import net.minecraft.resources.ResourceLocation;

public interface MultiSceneBuilder {
	MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
									PonderStoryBoard storyBoard);

	MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
									PonderStoryBoard storyBoard, ResourceLocation... tags);

	MultiSceneBuilder addStoryBoard(ResourceLocation schematicLocation,
									PonderStoryBoard storyBoard,
									Consumer<StoryBoardEntry> extras);

	MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard);

	MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard,
									ResourceLocation... tags);

	MultiSceneBuilder addStoryBoard(String schematicPath, PonderStoryBoard storyBoard,
									Consumer<StoryBoardEntry> extras);
}
