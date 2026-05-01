package net.createmod.ponder1710.api.registration;

import java.util.function.Function;

import net.createmod.ponder1710.api.scene.PonderStoryBoard;
import net.minecraft.util.ResourceLocation;

public interface PonderSceneRegistrationHelper<T> {

    <S> PonderSceneRegistrationHelper<S> withKeyFunction(Function<S, T> keyGen);

    StoryBoardEntry addStoryBoard(T component, ResourceLocation schematicLocation, PonderStoryBoard storyBoard, ResourceLocation... tags);

    StoryBoardEntry addStoryBoard(T component, String schematicPath, PonderStoryBoard storyBoard, ResourceLocation... tags);

    MultiSceneBuilder forComponents(T... components);

    MultiSceneBuilder forComponents(Iterable<? extends T> components);

    ResourceLocation asLocation(String path);
}
