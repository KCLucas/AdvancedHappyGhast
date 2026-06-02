# Advanced Happy Ghast (Fabric)

**Advanced Happy Ghast** is a feature-rich expansion that transforms the Happy Ghast into a loyal, progressing companion. Instead of being just a simple mount, your Ghast now gains its own identity, tracks your journeys, and grows more powerful as you train it.

## Core Features:

###  Ownership & Progression
The first player to ride a wild Happy Ghast becomes its permanent **Owner**. Only the owner can train and upgrade the creature.
* **Level 0 → 1:** Gain experience simply by traveling distance with your Ghast.
* **Level 1 → 2:** Feed it **25 Ghast Tears** through the upgrade slot to unlock the next stage.
* **Level 2 → 3:** Offer a **Nether Star** to reach the pinnacle of Ghast evolution.

###  Integrated Dashboard (GUI)
No more floating text boxes! Simply press **'E'** while riding to open the dedicated **Ghast Menu**.
* **Status Monitor:** View the current owner, the Ghast's level, and the **total distance** you have traveled together.
* **Requirement Tracker:** Live updates showing exactly what is still needed for the next level-up.
* **Upgrade Slot:** A specialized slot for level-up materials and (later) ammunition.

###  Speed &  Storage
Leveling up your Ghast provides tangible benefits for your travels:
* **Speed Bonuses (Lv 1-3):** With every level reached, your Ghast becomes permanently faster, allowing for rapid traversal of the world.
* **Inventory Expansion (Lv 2-3):** Starting at Level 2, your Ghast unlocks internal storage rows. Level 3 provides the maximum capacity.
All items and statistics are safely stored via NBT and persist through world restarts.

###  Server Administration
Optimized for server owners, all settings are saved **per world**.
* `/aghast status`: View the current server-wide requirements and bonuses.
* `/aghast set distance/speed`: Fine-tune progression to fit your gameplay style.
* `/aghast set fireballs`: (Pre-configured) Toggle the upcoming combat mode (Off/Fire/Damage/Full).
* `/aghast spawn <lvl>`: Summon a specific level for testing or events.

---

## Technical Details
* Requires **Fabric API**.
* Native support for **Minecraft 1.21.1**.
* Designed to work with the standard Happy Ghast entity.

---
*Note: This is an early version. Combat mechanics and fireball ammunition usage are currently in development!*
