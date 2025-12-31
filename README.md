[![modrinth](https://img.shields.io/badge/Modrinth-Exile%20Respawn-black?style=for-the-badge&logo=modrinth&labelColor=snow)](https://modrinth.com/mod/exile-respawn)
[![Build](https://github.com/haruomaki/exile-respawn/actions/workflows/build.yml/badge.svg)](https://github.com/haruomaki/exile-respawn/actions/workflows/build.yml)


# Exile Respawn

**Death does not send you back home.  
It sends you away.**

Exile Respawn is a Minecraft mod that changes how respawning works.  
When enabled, **players respawn far away from their death location**, regardless spawn points.

This behavior is controlled via a gamerule, allowing full flexibility for singleplayer and servers.

The mod is philosophically inspired by **Better Than Wolves** and **Compassionate Hardcore**.

---

## What This Mod Does

When the Exile Respawn gamerule is enabled:

- Any player death causes a **distant respawn**
- The respawn location is:
  - Far away from the previous position
  - Chosen to avoid immediate lethal hazards (lava, void, fatal drops, etc.)
- This applies to:
  - Survival
  - Multiplayer servers

No items are preserved.  
No teleportation back is provided.

You die — and you start somewhere else.

---

## Gamerule

Exile Respawn is enabled by default.

Enable it with:

```mcfunction
/gamerule exileRespawn true
```
