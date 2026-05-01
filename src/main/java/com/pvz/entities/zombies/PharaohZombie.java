package com.pvz.entities.zombies;

public class PharaohZombie extends Zombie {
    public static final int BASE_HEALTH = 400;
    public static final int ARMOR_HEALTH = 300;

    private int armorHealth;
    private boolean armorBroken;
    private boolean armorBreakAnim;
    private long armorBreakStart;
    private static final long ARMOR_BREAK_DURATION = 1200;

    public PharaohZombie(double x, double y) {
        super(x, y, 40, 60, BASE_HEALTH, 0.17, 28, 35);
        this.armorHealth = ARMOR_HEALTH;
        this.armorBroken = false;
        this.armorBreakAnim = false;
    }

    @Override
    public void update() {
        if (armorBreakAnim && System.currentTimeMillis() - armorBreakStart > ARMOR_BREAK_DURATION) {
            armorBreakAnim = false;
        }
        super.update();
    }

    @Override
    public void takeFrontalDamage(int damage) {
        if (!armorBroken) {
            damageArmor(damage);
            return;
        }
        super.takeFrontalDamage(damage);
    }

    @Override
    public void takeLobbedDamage(int damage) {
        if (!armorBroken) {
            damageArmor(damage);
            return;
        }
        super.takeLobbedDamage(damage);
    }

    @Override
    public void takeBelowDamage(int damage) {
        if (!armorBroken) {
            damageArmor(damage);
            return;
        }
        super.takeBelowDamage(damage);
    }

    @Override
    public void takeBehindDamage(int damage) {
        if (!armorBroken) {
            damageArmor(damage);
            return;
        }
        super.takeBehindDamage(damage);
    }

    private void damageArmor(int damage) {
        armorHealth -= damage;
        if (armorHealth <= 0) {
            int overflow = -armorHealth;
            armorHealth = 0;
            armorBroken = true;
            armorBreakAnim = true;
            armorBreakStart = System.currentTimeMillis();
            if (overflow > 0) {
                super.takeFrontalDamage(overflow);
            }
        }
    }

    public int getArmorHealth() {
        return armorHealth;
    }

    public boolean isArmorBroken() {
        return armorBroken;
    }

    public boolean isArmorBreakAnim() {
        return armorBreakAnim;
    }

    @Override
    public String toString() {
        return "PharaohZombie";
    }
}
