package application.items;

public enum WeaponType {
    PISTOL, ASSAULT_RIFLE, SUBMACHINE_GUN;

    public enum Pistol {
        GLOCK, DESERT_EAGLE;
    }

    public enum AssaultRifle {
        AK_47, M4;
    }

    public enum SubmachineGun {
        P90, UZI;
    }
}