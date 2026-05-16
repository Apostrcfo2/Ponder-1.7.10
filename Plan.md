# Ponder 1.7.10 — Porting Plan

> Port of the standalone [Ponder](https://github.com/Creators-of-Create/Ponder) library (1.21.1) to Minecraft 1.7.10,
> built on top of [MetaWorld Mixins](https://github.com/Gordon-Frohman/Metaworlds-Mixins) and [JOML](https://github.com/JOML-CI/JOML).

---

## Architecture Overview

```
Ponder 1.7.10
├── ponder1710/          ← Ponder source (ported from 1.21.1)
└── metanip/             ← Metanip (port of Catnip for 1.7.10)
```

**Metanip** is a port of catnip using:
- **MetaWorld Mixins** — SubWorld engine replacing `SchematicLevel`
- **JOML 1.10.8** — Matrix math replacing `PoseStack` / `com.mojang.math`
- **LWJGL 2 / GL11** — Rendering replacing `RenderSystem` / `GuiGraphics`
- **ForgeDirection** — Replacing `Direction` / `core.Direction`
- **StatCollector** — Replacing `I18n`
- **NBTTagCompound** — Replacing `CompoundTag`

---

## Key API Mappings (1.17+ → 1.7.10)

| Modern (1.17+) | 1.7.10 Equivalent |
| :--- | :--- |
| `Level` | `World` |
| `BlockEntity` | `TileEntity` |
| `BlockState` | `Block` + metadata (0–15) |
| `BlockPos` | `x, y, z` ints or `ChunkCoordinates` |
| `Direction` | `ForgeDirection` |
| `Vec3` (world.phys) | `Vec3` (util) via `Vec3.createVectorHelper()` |
| `AABB` | `AxisAlignedBB` |
| `ResourceLocation` (resources) | `ResourceLocation` (util) |
| `CompoundTag` | `NBTTagCompound` |
| `Entity` (world.entity) | `Entity` (entity) |
| `ItemStack` (world.item) | `ItemStack` (item) |
| `GuiGraphics` | `GL11` direct calls |
| `PoseStack` | `GL11.glPushMatrix()` / `GL11.glPopMatrix()` |
| `RenderType` | Not available — handled differently |
| `Camera` | Not available — simulated via GL11 |
| `I18n.get()` | `StatCollector.translateToLocal()` |
| `ChatFormatting` | `EnumChatFormatting` |
| `Parrot` entity | Not in 1.7.10 (Potential future EFR Integration) |
| `Component` / `MutableComponent` | Plain `String` |
| `Brigadier` commands | `ICommand` / `CommandBase` |
| `ResourceLocation.fromNamespaceAndPath()` | `new ResourceLocation(ns, path)` |
| `ResourceLocation.getNamespace()` | `.getResourceDomain()` |
| `ResourceLocation.getPath()` | `.getResourcePath()` |
| `entity.isAlive()` | `!entity.isDead` |
| `Vec3.ZERO` | `Vec3.createVectorHelper(0, 0, 0)` |
| `vec.x / .y / .z` | `vec.xCoord / .yCoord / .zCoord` |
| `Mth` | `MathHelper` |
| `record` keyword | Class with fields (Java 8) |
| `LerpedFloat` (catnip) | `LerpedFloat` (metanip) |
| `Couple` / `Pair` (catnip) | `Couple` / `Pair` (metanip) |
| `VecHelper` (catnip) | `VecHelper` (metanip) |
| `Outliner` (catnip) | `Outliner` (metanip) |
| `SchematicLevel` (catnip) | `SubWorldClient` (MetaWorld Mixins) |

---

## Phase 1 — Comment Everything ✅ COMPLETE

Go through all **139 Java files** in `ponder1710/`, commenting out any code that depends on APIs not available in 1.7.10. Never delete — only comment, following Gordon-Frohman's backporting guidelines.

### Files Reviewed
- [x] All files in `ponder1710/` have been reviewed and commented.
---

## Phase 2 — Port Metanip ⏳ IN PROGRESS

Port the essential parts of Catnip to 1.7.10 as **Metanip** (`net.createmod.metanip`).
Files that Ponder does not use can be ignored or stubbed.

### Dependencies Available
- ✅ **JOML 1.10.8** — Matrix4f, Vector3f, Vector4f (replaces `org.joml` from modern MC)
- ✅ **MetaWorld Mixins** — SubWorld system (replaces `SchematicLevel`)
- ✅ **UniMixins** — Mixin support for 1.7.10
- ✅ **GL11 / LWJGL 2** — All rendering

### Files to Port (by priority)

#### `data/` — No dependencies, port first
- [ ] `Couple.java`
- [ ] `Pair.java`
- [ ] `Iterate.java`
- [ ] `TriState.java`
- [ ] `FunctionalHelper.java`
- [ ] `IntAttached.java`
- [ ] `LongAttached.java`

#### `animation/` — Core of Ponder's smooth animation
- [ ] `LerpedFloat.java`
- [ ] `AnimationTickHolder.java`
- [ ] `AnimationFunctions.java`
- [ ] `PhysicalFloat.java`

#### `math/` — Used throughout Ponder
- [ ] `VecHelper.java`
- [ ] `AngleHelper.java`
- [ ] `Pointing.java`
- [ ] `DirectionHelper.java`
- [ ] `BBHelper.java`
- [ ] `BlockFace.java`

#### `theme/`
- [ ] `Color.java`

#### `lang/` — Text rendering
- [ ] `LangBuilder.java`
- [ ] `Lang.java`
- [ ] `FontHelper.java`
- [ ] `ClientFontHelper.java`

#### `registry/`
- [ ] `RegisteredObjectsHelper.java`

#### `gui/` — UI system
- [ ] `NavigatableSimiScreen.java`
- [ ] `AbstractSimiScreen.java`
- [ ] `ScreenOpener.java`
- [ ] `UIRenderHelper.java`
- [ ] `TextureSheetSegment.java`
- [ ] `element/ScreenElement.java`
- [ ] `element/BoxElement.java`
- [ ] `element/GuiGameElement.java`
- [ ] `widget/AbstractSimiWidget.java`
- [ ] `widget/BoxWidget.java`

#### `outliner/` — Block/AABB outlines in world
- [ ] `Outline.java`
- [ ] `Outliner.java`
- [ ] `AABBOutline.java`
- [ ] `ChasingAABBOutline.java`
- [ ] `BlockClusterOutline.java`
- [ ] `LineOutline.java`

#### `levelWrappers/` — Virtual world for Ponder scenes
- [ ] `SchematicLevel.java` ← backed by MetaWorld's SubWorldClient
- [ ] `WorldHelper.java`
- [ ] `RayTraceLevel.java`

#### `render/` — Block/entity rendering pipeline
- [ ] `SuperByteBuffer.java`
- [ ] `SuperByteBufferCache.java`
- [ ] `SuperRenderTypeBuffer.java`
- [ ] `DefaultSuperRenderTypeBuffer.java`

#### Not needed for Ponder (skip or stub)
- `codecs/` — DataFixers not in 1.7.10
- `components/` — Data components not in 1.7.10
- `placement/` — Not used by Ponder
- `net/` — Networking system different in 1.7.10
- `ghostblock/` — Not used by Ponder
- `config/ui/` — Simplified to plain fields

---

## Phase 3 — Adapt Ponder Code ⏳ PENDING

With Metanip fully ported, go back through every commented section in `ponder1710/` and replace with working 1.7.10 code using Metanip APIs.

### Key Tasks

#### Rendering System
- [ ] Port `PonderScene.renderScene()` using GL11 + JOML
- [ ] Port `PonderScene.SceneTransform` matrix math using JOML `Matrix4f`
- [ ] Port `WorldSectionElementImpl` block rendering using 1.7.10 `RenderBlocks`
- [ ] Port `WorldSectionElementImpl` TileEntity rendering using 1.7.10 `TileEntityRendererDispatcher`
- [ ] Port `PonderUI.renderScene()` full rendering pipeline

#### World System
- [ ] Integrate `PonderLevel` with MetaWorld's `SubWorldClient`
- [ ] Implement schematic loading using 1.7.10 NBT (`.nbt` → `NBTTagCompound`)
- [ ] Implement block backup/restore using `Block` + metadata
- [ ] Implement TileEntity backup/restore using `NBTTagCompound`

#### UI System
- [ ] Port `PonderUI` full screen using GL11
- [ ] Port `PonderProgressBar` using GL11
- [ ] Port `PonderButton` / `BoxWidget` using GL11
- [ ] Port `TextWindowElement` text rendering using `FontRenderer`
- [ ] Port `InputWindowElement` using GL11
- [ ] Implement `NavigatableSimiScreen` breadcrumb navigation

#### Particle System
- [ ] Port `PonderWorldParticles` using 1.7.10 `EffectRenderer`

#### Command System
- [ ] Port `PonderCommand` using `ICommand` / `CommandBase`

#### Special: Parrot Integration (Et Futurum Requiem) — Future Consideration
- [ ] Evaluate and plan for optional integration with Et Futurum Requiem for Parrot entity support.
- [ ] This task is not a current priority but will be revisited upon completion of core Ponder porting.

#### Remaining Tasks
- [ ] Implement `StructureTemplate` equivalent for schematic loading.
- [ ] Port `Outliner` integration for selection highlighting.

---

## Dependencies

| Dependency | Version | Purpose |
| :--- | :--- | :--- |
| Minecraft Forge | 1.7.10-10.13.4.1614 | Base modding API |
| UniMixins | 0.1.14 | Mixin support |
| MetaWorld Mixins | 1.6.1a | SubWorld engine |
| JOML | 1.10.8 | Matrix math |
| Et Futurum Requiem | Optional | Potential future Parrot & content support |

---

## Notes

- **Parrot entity** — Integration with Et Futurum Requiem is a future consideration, not an immediate task.
- **Metanip** — Porting is in progress; focus remains on completing this phase.
- **JOML** is explicitly added for 1.7.10 to handle modern matrix math.
