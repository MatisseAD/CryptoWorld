#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import json
import math
import random
from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError as exc:  # pragma: no cover - build-time dependency guard
    raise SystemExit("Pillow is required for generate_styled_assets.py") from exc


ROOT = Path(__file__).resolve().parents[2]
MANIFEST_PATH = ROOT / "src" / "main" / "resources" / "cryptoworld_assetgen" / "styled_assets.json"
TEXTURE_ROOT = ROOT / "src" / "main" / "resources" / "assets" / "cryptoworld" / "textures"
PREVIEW_PATH = Path(__file__).resolve().parent / "preview.png"
SIZE = 16
SCALE = 16
PREVIEW_COLUMNS = 4


def hex_to_rgb(value: str) -> tuple[int, int, int]:
    value = value.lstrip("#")
    return tuple(int(value[index:index + 2], 16) for index in (0, 2, 4))


def mix(color_a: tuple[int, int, int], color_b: tuple[int, int, int], amount: float) -> tuple[int, int, int]:
    return tuple(
        max(0, min(255, round(component_a + (component_b - component_a) * amount)))
        for component_a, component_b in zip(color_a, color_b)
    )


def brighten(color: tuple[int, int, int], amount: float) -> tuple[int, int, int]:
    return mix(color, (255, 255, 255), amount)


def darken(color: tuple[int, int, int], amount: float) -> tuple[int, int, int]:
    return mix(color, (0, 0, 0), amount)


def rng_for(name: str) -> random.Random:
    seed = int(hashlib.sha256(name.encode("utf-8")).hexdigest()[:16], 16)
    return random.Random(seed)


def blank_image() -> Image.Image:
    return Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))


def draw_gradient(image: Image.Image, palette: dict[str, tuple[int, int, int]], invert: bool = False) -> None:
    for y in range(SIZE):
        factor = y / (SIZE - 1)
        if invert:
            factor = 1.0 - factor
        row_color = mix(palette["mid"], palette["base"], factor * 0.65)
        for x in range(SIZE):
            image.putpixel((x, y), row_color + (255,))


def add_dither(image: Image.Image, palette: dict[str, tuple[int, int, int]], rng: random.Random, strength: int = 18) -> None:
    for y in range(SIZE):
        for x in range(SIZE):
            red, green, blue, alpha = image.getpixel((x, y))
            noise = rng.randint(-strength, strength)
            modifier = 0.4 if (x + y) % 2 == 0 else -0.2
            image.putpixel(
                (x, y),
                (
                    max(0, min(255, round(red + noise * modifier))),
                    max(0, min(255, round(green + noise * modifier))),
                    max(0, min(255, round(blue + noise * modifier))),
                    alpha,
                ),
            )


def draw_bevel(image: Image.Image, palette: dict[str, tuple[int, int, int]]) -> None:
    for x in range(SIZE):
        image.putpixel((x, 0), brighten(palette["light"], 0.2) + (255,))
        image.putpixel((x, SIZE - 1), darken(palette["shadow"], 0.15) + (255,))
    for y in range(SIZE):
        image.putpixel((0, y), brighten(palette["light"], 0.15) + (255,))
        image.putpixel((SIZE - 1, y), darken(palette["shadow"], 0.1) + (255,))


def draw_circuit_lines(draw: ImageDraw.ImageDraw, palette: dict[str, tuple[int, int, int]], rng: random.Random) -> None:
    accent = brighten(palette["accent"], 0.05)
    glow = palette["glow"]
    y_positions = [3, 7, 11]
    for y in y_positions:
        draw.line((2, y, 13, y), fill=accent + (255,))
    for x in (4, 8, 12):
        draw.line((x, 2, x, 13), fill=darken(palette["mid"], 0.15) + (255,))
        draw.point((x, 7), fill=glow + (255,))

    for _ in range(5):
        x = rng.randint(2, 13)
        y = rng.randint(2, 13)
        draw.point((x, y), fill=brighten(glow, 0.25) + (255,))


def generate_alloy_block(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    rng = rng_for(name)
    image = blank_image()
    draw_gradient(image, palette)
    add_dither(image, palette, rng)
    draw_bevel(image, palette)
    draw = ImageDraw.Draw(image)

    for offset in range(-8, 16, 5):
        draw.line((max(1, offset), 1, min(14, offset + 8), 14), fill=brighten(palette["mid"], 0.1) + (180,))
    draw_circuit_lines(draw, palette, rng)
    draw.rectangle((5, 5, 10, 10), outline=brighten(palette["light"], 0.25) + (220,), fill=darken(palette["base"], 0.1) + (140,))
    return image


def generate_crystal_block(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    rng = rng_for(name)
    image = blank_image()
    draw_gradient(image, palette, invert=True)
    add_dither(image, palette, rng, strength=12)
    draw_bevel(image, palette)
    draw = ImageDraw.Draw(image)

    crystal_points = [
        [(3, 12), (6, 3), (8, 8), (6, 13)],
        [(8, 13), (10, 4), (13, 9), (11, 14)],
        [(2, 8), (4, 5), (5, 10), (3, 11)],
    ]
    for polygon in crystal_points:
        fill = brighten(palette["light"], rng.uniform(0.0, 0.15))
        draw.polygon(polygon, fill=fill + (220,), outline=palette["accent"] + (255,))

    for _ in range(14):
        x = rng.randint(2, 13)
        y = rng.randint(2, 13)
        draw.point((x, y), fill=brighten(palette["glow"], 0.2) + (255,))
    return image


def stone_color(x: int, y: int) -> tuple[int, int, int]:
    base = (84, 92, 102)
    modifier = ((x * 13 + y * 7) % 19) - 9
    return tuple(max(0, min(255, channel + modifier)) for channel in base)


def generate_ore(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    rng = rng_for(name)
    image = blank_image()
    draw = ImageDraw.Draw(image)

    for y in range(SIZE):
        for x in range(SIZE):
            color = stone_color(x, y)
            if (x + y) % 5 == 0:
                color = brighten(color, 0.08)
            image.putpixel((x, y), color + (255,))

    draw_bevel(image, {"light": (166, 176, 184), "shadow": (42, 48, 54), "base": (88, 96, 104), "mid": (88, 96, 104), "accent": (88, 96, 104), "glow": (88, 96, 104)})

    for _ in range(8):
        center_x = rng.randint(2, 13)
        center_y = rng.randint(2, 13)
        chunk = [
            (center_x, center_y - 1),
            (center_x + 1, center_y),
            (center_x, center_y + 1),
            (center_x - 1, center_y),
        ]
        draw.polygon(chunk, fill=palette["mid"] + (255,), outline=palette["accent"] + (255,))
        draw.point((center_x, center_y), fill=brighten(palette["glow"], 0.25) + (255,))
    return image


def generate_machine_top(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    rng = rng_for(name)
    image = blank_image()
    draw_gradient(image, palette)
    add_dither(image, palette, rng, strength=10)
    draw_bevel(image, palette)
    draw = ImageDraw.Draw(image)

    draw.rounded_rectangle((3, 3, 12, 12), radius=2, fill=darken(palette["base"], 0.12) + (255,), outline=palette["light"] + (255,))
    for x in range(5, 11, 2):
        draw.line((x, 5, x, 10), fill=palette["glow"] + (255,))
    draw.rectangle((5, 11, 10, 12), fill=brighten(palette["accent"], 0.1) + (255,))
    return image


def generate_machine_side(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    rng = rng_for(name)
    image = blank_image()
    draw_gradient(image, palette)
    add_dither(image, palette, rng, strength=14)
    draw_bevel(image, palette)
    draw = ImageDraw.Draw(image)

    draw.rectangle((2, 3, 13, 12), fill=darken(palette["base"], 0.05) + (255,), outline=brighten(palette["mid"], 0.1) + (255,))
    for x in range(4, 12, 2):
        draw.line((x, 5, x, 10), fill=darken(palette["shadow"], 0.1) + (255,))
    draw.rectangle((3, 4, 5, 11), fill=palette["glow"] + (210,))
    draw.line((7, 4, 12, 4), fill=palette["accent"] + (255,))
    draw.line((7, 11, 12, 11), fill=palette["accent"] + (255,))
    return image


def frame_item(image: Image.Image, palette: dict[str, tuple[int, int, int]], inner_drawer) -> Image.Image:
    draw = ImageDraw.Draw(image)
    draw.rounded_rectangle((1, 1, 14, 14), radius=3, fill=darken(palette["base"], 0.08) + (230,), outline=brighten(palette["light"], 0.1) + (255,))
    draw.rounded_rectangle((3, 3, 12, 12), radius=2, fill=darken(palette["shadow"], 0.05) + (240,))
    inner_drawer(draw, palette)
    return image


def generate_coin_btc(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.ellipse((3, 3, 12, 12), fill=colors["mid"] + (255,), outline=brighten(colors["light"], 0.1) + (255,))
        draw.line((7, 4, 7, 12), fill=colors["accent"] + (255,))
        draw.line((5, 5, 9, 5), fill=colors["accent"] + (255,))
        draw.line((5, 8, 9, 8), fill=colors["accent"] + (255,))
        draw.line((5, 11, 9, 11), fill=colors["accent"] + (255,))
        draw.point((10, 6), fill=brighten(colors["glow"], 0.2) + (255,))

    return frame_item(image, palette, inner)


def generate_coin_eth(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.ellipse((3, 3, 12, 12), fill=darken(colors["mid"], 0.15) + (255,), outline=brighten(colors["light"], 0.2) + (255,))
        draw.polygon([(8, 4), (5, 8), (8, 9), (11, 8)], fill=colors["light"] + (255,), outline=colors["accent"] + (255,))
        draw.polygon([(8, 10), (5, 9), (8, 13), (11, 9)], fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
    return frame_item(image, palette, inner)


def generate_ingot(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.polygon([(4, 10), (6, 6), (11, 6), (13, 10), (11, 12), (6, 12)], fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
        draw.line((6, 7, 11, 7), fill=brighten(colors["light"], 0.15) + (255,))
    return frame_item(image, palette, inner)


def generate_crystal_item(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.polygon([(8, 3), (5, 8), (7, 13), (10, 10), (11, 5)], fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
        draw.line((7, 5, 9, 10), fill=brighten(colors["light"], 0.2) + (255,))
    return frame_item(image, palette, inner)


def generate_charged_crystal(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = generate_crystal_item(name, palette)
    draw = ImageDraw.Draw(image)
    draw.line((3, 5, 13, 5), fill=palette["glow"] + (255,))
    draw.line((5, 3, 11, 13), fill=brighten(palette["glow"], 0.15) + (220,))
    return image


def generate_chip(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.rectangle((4, 4, 11, 11), fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
        for x in range(4, 12, 2):
            draw.line((x, 2, x, 4), fill=colors["light"] + (255,))
            draw.line((x, 11, x, 13), fill=colors["light"] + (255,))
        for y in range(4, 12, 2):
            draw.line((2, y, 4, y), fill=colors["light"] + (255,))
            draw.line((11, y, 13, y), fill=colors["light"] + (255,))
        draw.rectangle((6, 6, 9, 9), fill=colors["glow"] + (200,))
    return frame_item(image, palette, inner)


def generate_gpu(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.rectangle((3, 5, 12, 10), fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
        draw.ellipse((5, 6, 8, 9), outline=colors["glow"] + (255,), fill=darken(colors["shadow"], 0.05) + (255,))
        draw.ellipse((8, 6, 11, 9), outline=colors["glow"] + (255,), fill=darken(colors["shadow"], 0.05) + (255,))
        draw.rectangle((12, 6, 13, 9), fill=colors["light"] + (255,))
        draw.line((4, 11, 11, 11), fill=colors["light"] + (255,))
    return frame_item(image, palette, inner)


def generate_detector(name: str, palette: dict[str, tuple[int, int, int]]) -> Image.Image:
    image = blank_image()

    def inner(draw: ImageDraw.ImageDraw, colors: dict[str, tuple[int, int, int]]) -> None:
        draw.rounded_rectangle((4, 4, 10, 11), radius=2, fill=colors["mid"] + (255,), outline=colors["accent"] + (255,))
        draw.rectangle((5, 5, 9, 7), fill=colors["glow"] + (255,))
        draw.line((7, 3, 11, 1), fill=colors["light"] + (255,))
        draw.line((10, 8, 12, 10), fill=colors["light"] + (255,))
        draw.line((6, 11, 5, 14), fill=colors["light"] + (255,))
    return frame_item(image, palette, inner)


GENERATORS = {
    "alloy_block": generate_alloy_block,
    "crystal_block": generate_crystal_block,
    "ore": generate_ore,
    "machine_top": generate_machine_top,
    "machine_side": generate_machine_side,
    "coin_btc": generate_coin_btc,
    "coin_eth": generate_coin_eth,
    "ingot": generate_ingot,
    "crystal_item": generate_crystal_item,
    "charged_crystal": generate_charged_crystal,
    "chip": generate_chip,
    "gpu": generate_gpu,
    "detector": generate_detector,
}


def save_texture(output_path: Path, image: Image.Image) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    image.save(output_path)


def make_preview(entries: list[tuple[str, Image.Image]]) -> None:
    if not entries:
        return

    rows = math.ceil(len(entries) / PREVIEW_COLUMNS)
    preview = Image.new("RGBA", (PREVIEW_COLUMNS * (SIZE * SCALE + 12), rows * (SIZE * SCALE + 12)), (9, 14, 22, 255))

    for index, (_, image) in enumerate(entries):
        x = index % PREVIEW_COLUMNS
        y = index // PREVIEW_COLUMNS
        cell_x = x * (SIZE * SCALE + 12) + 6
        cell_y = y * (SIZE * SCALE + 12) + 6
        preview.paste(image.resize((SIZE * SCALE, SIZE * SCALE), Image.Resampling.NEAREST), (cell_x, cell_y))

    PREVIEW_PATH.parent.mkdir(parents=True, exist_ok=True)
    preview.save(PREVIEW_PATH)


def load_manifest() -> dict:
    return json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))


def main() -> None:
    manifest = load_manifest()
    palettes = {name: {key: hex_to_rgb(value) for key, value in colors.items()} for name, colors in manifest["palettes"].items()}
    preview_entries: list[tuple[str, Image.Image]] = []

    for texture in manifest["textures"]:
        palette = palettes[texture["palette"]]
        generator = GENERATORS[texture["kind"]]
        image = generator(texture["path"], palette)
        save_texture(TEXTURE_ROOT / f"{texture['path']}.png", image)
        preview_entries.append((texture["path"], image))

    make_preview(preview_entries)
    print(f"Generated {len(preview_entries)} stylized textures from {MANIFEST_PATH}")


if __name__ == "__main__":
    main()
