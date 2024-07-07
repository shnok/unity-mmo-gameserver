package com.shnok.javaserver.model.skills;

public class FormulasLegacy {
    private static FormulasLegacy _instance;
    public static FormulasLegacy getInstance() {
        if(_instance == null) {
            _instance = new FormulasLegacy();
        }
        return _instance;
    }

    // Calculate delay (in milliseconds) before next ATTACK
    public final int calcPAtkSpd(double atkSpeed) {
        // Source L2J
        // measured Oct 2006 by Tank6585, formula by Sami
        // attack speed 312 equals 1500 ms delay... (or 300 + 40 ms delay?)
        if (atkSpeed < 2) {
            return 2700;
        }
        return (int) (470000 / atkSpeed);
    }

    // Calculate delay (in milliseconds) for skills cast
//    public final int calcMAtkSpd(Skill skill, double skillTime) {
//        if (skill.isMagic()) {
//            return (int) ((skillTime * 333) / attacker.getMAtkSpd());
//        }
//        return (int) ((skillTime * 333) / attacker.getPAtkSpd());
//    }
}
