package org.plugin.hardworlds.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CustomMobsListener implements Listener {
    private final Plugin plugin;
    private final Random random;

    public CustomMobsListener(Plugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Mob) {
            Mob mob = (Mob) event.getEntity();

            // Customize mob attributes
            customizeMobAttributes(mob);

            // Equip mob with random gear and enchantments if applicable
            if (shouldEquipWithGear(mob)) {
                equipMobWithRandomGear(mob);
            }

            // Add special abilities to the mob
            addSpecialAbilities(mob);

            // Enhance AI behavior
            enhanceMobAI(mob);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Mob) {
            Player player = (Player) event.getEntity();
            Mob mob = (Mob) event.getDamager();

            // Apply special effects on player when hit by certain mobs
            applySpecialEffects(player, mob);
        }
    }

    private void customizeMobAttributes(Mob mob) {
        if (mob.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue() * 2);
            mob.setHealth(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        }
        if (mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue() * 1.5);
        }
        if (mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getBaseValue() * 2);
        }
    }

    private boolean shouldEquipWithGear(Mob mob) {
        return mob != null || null instanceof Piglin || null instanceof PiglinBrute || null instanceof Skeleton || null instanceof Illager;
    }

    private void equipMobWithRandomGear(Mob mob) {
        List<Material> helmetMaterials = Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET);
        List<Material> chestplateMaterials = Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
        List<Material> leggingsMaterials = Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
        List<Material> bootsMaterials = Arrays.asList(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS);
        List<Enchantment> enchantments = Arrays.asList(Enchantment.BLAST_PROTECTION, Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION);

        // Equip with random helmet
        ItemStack helmet = new ItemStack(helmetMaterials.get(random.nextInt(helmetMaterials.size())));
        applyRandomEnchantments(helmet, enchantments);
        Objects.requireNonNull(mob.getEquipment()).setHelmet(helmet);

        // Equip with random chestplate
        ItemStack chestplate = new ItemStack(chestplateMaterials.get(random.nextInt(chestplateMaterials.size())));
        applyRandomEnchantments(chestplate, enchantments);
        mob.getEquipment().setChestplate(chestplate);

        // Equip with random leggings
        ItemStack leggings = new ItemStack(leggingsMaterials.get(random.nextInt(leggingsMaterials.size())));
        applyRandomEnchantments(leggings, enchantments);
        mob.getEquipment().setLeggings(leggings);

        // Equip with random boots
        ItemStack boots = new ItemStack(bootsMaterials.get(random.nextInt(bootsMaterials.size())));
        applyRandomEnchantments(boots, enchantments);
        mob.getEquipment().setBoots(boots);

        // Equip with a weapon
        ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);
        applyRandomEnchantments(weapon, Arrays.asList(Enchantment.SHARPNESS, Enchantment.FIRE_ASPECT));
        mob.getEquipment().setItemInMainHand(weapon);
    }

    private void applyRandomEnchantments(ItemStack item, List<Enchantment> enchantments) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
            int level = random.nextInt(3) + 1; // Random level between 1 and 3
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
    }

    private void addSpecialAbilities(Mob mob) {
        switch (mob.getType()) {
            case BLAZE:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                break;
            case CREEPER:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                break;
            case DROWNED:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 1));
                break;
            case ELDER_GUARDIAN:
            case EVOKER:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                break;
            case ENDER_DRAGON:
            case WITHER:
            case WARDEN:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 2));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
                break;
            case ENDERMITE:
            case SILVERFISH:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                break;
            case GHAST:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 1));
                break;
            case GIANT:
            case RAVAGER:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 2));
                break;
            case GUARDIAN:
            case ZOMBIE_VILLAGER:
            case ZOMBIE:
            case SKELETON:
            case PILLAGER:
            case HUSK:
            case STRAY:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                break;
            case HOGLIN:
            case ZOGLIN:
            case VINDICATOR:
            case SLIME:
            case PIGLIN_BRUTE:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                break;
            case MAGMA_CUBE:
            case ZOMBIFIED_PIGLIN:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
                break;
            case PHANTOM:
            case VEX:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                break;
            case SHULKER:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 1));
                break;
            case WITCH:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                break;
            case WITHER_SKELETON:
                mob.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1));
                mob.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
                break;
            default:
                break;
        }
    }

    private void enhanceMobAI(Mob mob) {
        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(50.0);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (mob.isDead()) {
                return;
            }

            if (mob.getTarget() instanceof Player) {
                Player target = (Player) mob.getTarget();
                if (target != null && random.nextDouble() < 0.1) { // 10% chance to call for reinforcements
                    callForReinforcements(mob, target);
                }
            }
        }, 0L, 200L); // Every 10 seconds
    }

    private void applySpecialEffects(Player player, Mob mob) {
        if (mob.getType() == EntityType.SPIDER) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
        }

        if (mob.getType() == EntityType.ENDERMAN) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        }
    }

    private void callForReinforcements(Mob mob, Player target) {
        for (int i = 0; i < 3; i++) { // Spawn 3 reinforcements
            Mob reinforcement = (Mob) target.getWorld().spawnEntity(target.getLocation().add(random.nextInt(10) - 5, 0, random.nextInt(10) - 5), mob.getType());
            customizeMobAttributes(reinforcement);
            if (shouldEquipWithGear(reinforcement)) {
                equipMobWithRandomGear(reinforcement);
            }
            addSpecialAbilities(reinforcement);
            reinforcement.setTarget(target);
        }
    }
}
