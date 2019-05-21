package ru.hse.lyubortk.cannon;

/** Different available shell types */
public enum ShellType {
    SMALL(400, 3,10),
    MEDIUM(300, 6,20),
    BIG(200, 8,40);

    private double startSpeed;
    private double bulletRadius;
    private double explosionRadius;

    ShellType(double speed, double bulletRadius, double explosionRadius) {
        this.startSpeed = speed;
        this.bulletRadius = bulletRadius;
        this.explosionRadius = explosionRadius;
    }

    public double getStartSpeed() {
        return startSpeed;
    }

    public double getBulletRadius() {
        return bulletRadius;
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }
}
