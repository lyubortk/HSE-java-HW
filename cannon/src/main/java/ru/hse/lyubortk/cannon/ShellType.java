package ru.hse.lyubortk.cannon;

enum ShellType {
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

    double getStartSpeed() {
        return startSpeed;
    }

    double getBulletRadius() {
        return bulletRadius;
    }

    double getExplosionRadius() {
        return explosionRadius;
    }
}
