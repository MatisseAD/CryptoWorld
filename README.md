# CryptoWorld

CryptoWorld est un mod Forge 1.20.4 centre sur une progression techno-crypto.

## Pipeline de blocs et textures stylises

Le projet contient maintenant un pipeline d'assets reutilisable :

- Le manifeste source est dans `src/main/resources/cryptoworld_assetgen/styled_assets.json`.
- Les textures PNG sont generees par `tools/assetgen/generate_styled_assets.py`.
- Les modeles et blockstates lisent le meme manifeste via `StylizedAssetManifest`.
- `processResources` lance automatiquement la generation de textures.

## Commandes utiles

```bash
./gradlew generateStyledAssets
./gradlew runData
./gradlew compileJava
```

Le script genere aussi un apercu local dans `tools/assetgen/preview.png`.

## Ajouter un nouveau bloc stylise

1. Enregistrer le bloc dans `ModBlocks`.
2. Ajouter son entree dans `blockModels` du manifeste.
3. Ajouter ses textures procedurales dans `textures` du manifeste.
4. Lancer `./gradlew runData`.

Les layouts pris en charge aujourd'hui sont `cube_all` et `cube_bottom_top`.
