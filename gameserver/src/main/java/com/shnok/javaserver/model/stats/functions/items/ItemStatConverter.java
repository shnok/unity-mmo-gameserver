package com.shnok.javaserver.model.stats.functions.items;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ItemStatConverter {

    public static List<AbstractFunction> parseArmor(DBArmor armor) {
        List<AbstractFunction> functions = new ArrayList<>();
        if(armor.getMpBonus() != 0) {
            functions.add(new FuncItemStatAdd(Stats.MAX_MP, 1, null, armor.getMpBonus(), null));
        }

        if(armor.getMDef() != 0) {
            functions.add(new FuncItemStatAdd(Stats.MAGIC_DEFENCE, 2, null, armor.getMDef(), null));
        }

        if(armor.getPDef() != 0) {
            functions.add(new FuncItemStatAdd(Stats.POWER_DEFENCE, 3, null, armor.getPDef(), null));
        }

        log.debug("Equipped armor has {} stat function(s).", functions.size());

        return functions;
    }

    public static List<AbstractFunction> parseWeapon(DBWeapon weapon) {
        List<AbstractFunction> functions = new ArrayList<>();

        if(weapon.getPAtk() != 0) {
            functions.add(new FuncItemStatSet(Stats.POWER_ATTACK, 0, null, weapon.getPAtk(), null));
        }

        if(weapon.getMAtk() != 0) {
            functions.add(new FuncItemStatSet(Stats.MAGIC_ATTACK, 0, null, weapon.getMAtk(), null));
        }

        if(weapon.getHitModify() != 0) {
            functions.add(new FuncItemStatAdd(Stats.POWER_ACCURACY_COMBAT, 2, null, weapon.getHitModify(), null));
        }

        if(weapon.getAvoidModify() != 0) {
            functions.add(new FuncItemStatAdd(Stats.POWER_EVASION_RATE, 2, null, weapon.getAvoidModify(), null));
        }

        if(weapon.getShieldDef() != 0) {
            functions.add(new FuncItemStatAdd(Stats.POWER_DEFENCE, 2, null, weapon.getShieldDef(), null));
        }

        if(weapon.getShieldDefRate() != 0) {
            functions.add(new FuncItemStatSet(Stats.SHIELD_RATE, 0, null, weapon.getShieldDefRate(), null));
        }

        if(weapon.getBaseAttackRange() != 0) {
            functions.add(new FuncItemStatSet(Stats.POWER_ATTACK_RANGE, 0, null, weapon.getBaseAttackRange(), null));
        }

        if(weapon.getAtkSpd() != 0) {
            functions.add(new FuncItemStatSet(Stats.POWER_ATTACK_SPEED, 0, null, weapon.getAtkSpd(), null));
        }

        if(weapon.getCritical() != 0) {
            functions.add(new FuncItemStatSet(Stats.CRITICAL_RATE, 0, null, weapon.getCritical(), null));
        }

        log.debug("Equipped weapon has {} stat function(s).", functions.size());

        return functions;
    }
}
