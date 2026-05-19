# XEIEXporter 0.2.0

## New Features
- Export all Ingredients registered in JEI.
  - This also exports materials added by other mods, as long as they are shown in the ingredient list.
  - However, translation keys cannot be obtained from the JEI API, so you need to register the `IngredientExtraHelper` described below.
- Added support for JEI subtypes.
- Added events.
  - `RegisterIngredientExtraHelperEvent`
    - Registers an `IngredientExtraHelper` to supplement data that each IngredientHelper cannot obtain.
  - `RegisterRecipeModifierEvent`
    - Registers a `RecipeModifier` that modifies recipes by recipe type.
  - `RegisterTagEvent`
    - Registers all tags for each Ingredient.
- Added `XEIExporterApi`.
  - This was created to ensure a certain level of compatibility even when the XEIEXporter version changes.
  - It is mainly intended to be used by `RecipeModifier`, but please create an issue if there is anything else you need.
- Added integration for vanilla Minecraft and Mekanism.

---
## 新要素
- JEIに登録されているIngredientを全てエクスポートするようにしました。
  - これにより、Mod独自の素材もエクスポートされます(一覧に表示されている場合)。
  - ただし、翻訳キーの取得はJEIのapiから取得できないので、後述の`IngredientExtraHelper`を登録する必要があります。
- JEIのサブタイプに対応しました。
- イベントを追加しました。
  - `RegisterIngredientExtraHelperEvent`
    - 各IngredientHelperで取得できない要素を補うための、`IngredientExtraHelper`を登録するイベント
  - `RegisterRecipeModifierEvent`
    - レシピタイプごとにレシピを改変する、`RecipeModifier`を登録するイベント
  - `RegisterTagEvent`
    - 各Ingredientのタグを全て登録するイベント
- `XEIExporterApi`を追加しました。
  - XEIEXporterのバージョンが変わっても、互換性がある程度保たれることを保証するために作成しました。
  - 主に`RecipeModifier`で使用されることを想定していますが、必要なものがあればIssueを作成していただけるとありがたいです。
- バニラ、Mekanismの連携を追加しました。
