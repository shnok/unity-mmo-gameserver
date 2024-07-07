package com.shnok.javaserver.model.stats;

import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;

import java.util.ArrayList;
import java.util.List;

// Source l2j

public final class Calculator {
    /** Empty Func table definition */
    private static final AbstractFunction[] EMPTY_FUNCS = new AbstractFunction[0];

    /** Table of Func object */
    private AbstractFunction[] _functions;

    /**
     * Constructor of Calculator (Init value : emptyFuncs).
     */
    public Calculator() {
        _functions = EMPTY_FUNCS;
    }

    /**
     * Constructor of Calculator (Init value : Calculator c).
     * @param c
     */
    public Calculator(Calculator c) {
        _functions = c._functions;
    }

    /**
     * Check if 2 calculators are equals.
     * @param c1
     * @param c2
     * @return
     */
    public static boolean equalsCals(Calculator c1, Calculator c2) {
        if (c1 == c2) {
            return true;
        }

        if ((c1 == null) || (c2 == null)) {
            return false;
        }

        AbstractFunction[] funcs1 = c1._functions;
        AbstractFunction[] funcs2 = c2._functions;

        if (funcs1 == funcs2) {
            return true;
        }

        if (funcs1.length != funcs2.length) {
            return false;
        }

        if (funcs1.length == 0) {
            return true;
        }

        for (int i = 0; i < funcs1.length; i++) {
            if (funcs1[i] != funcs2[i]) {
                return false;
            }
        }
        return true;

    }

    /**
     * Return the number of Funcs in the Calculator.
     * @return
     */
    public int size() {
        return _functions.length;
    }

    /**
     * Adds a function to the Calculator.
     * @param function the function
     */
    public synchronized void addFunc(AbstractFunction function) {
        AbstractFunction[] funcs = _functions;
        AbstractFunction[] tmp = new AbstractFunction[funcs.length + 1];

        final int order = function.getOrder();
        int i;

        for (i = 0; (i < funcs.length) && (order >= funcs[i].getOrder()); i++) {
            tmp[i] = funcs[i];
        }

        tmp[i] = function;

        for (; i < funcs.length; i++) {
            tmp[i + 1] = funcs[i];
        }

        _functions = tmp;
    }

    /**
     * Removes a function from the Calculator.
     * @param function the function
     */
    public synchronized void removeFunc(AbstractFunction function) {
        AbstractFunction[] funcs = _functions;
        AbstractFunction[] tmp = new AbstractFunction[funcs.length - 1];

        int i;

        for (i = 0; (i < (funcs.length - 1)) && (function != funcs[i]); i++) {
            tmp[i] = funcs[i];
        }

        if (i == funcs.length) {
            return;
        }

        for (i++; i < funcs.length; i++) {
            tmp[i - 1] = funcs[i];
        }

        if (tmp.length == 0) {
            _functions = EMPTY_FUNCS;
        } else {
            _functions = tmp;
        }
    }

    /**
     * Remove each Func with the specified owner of the Calculator.
     * @param owner the owner
     * @return a list of modified stats
     */
    public synchronized List<Stats> removeOwner(Object owner) {
        List<Stats> modifiedStats = new ArrayList<>();
        for (AbstractFunction func : _functions) {
            if (func.getFuncOwner() == owner) {
                modifiedStats.add(func.getStat());
                removeFunc(func);
            }
        }
        return modifiedStats;
    }

    /**
     * Run each function of the Calculator.
     * @param caster the caster
     * @param target the target
     * @param skill the skill
     * @param initVal the initial value
     * @return the calculated value
     */
//    public double calc(Entity caster, Entity target, Skill skill, double initVal) {
//        double value = initVal;
//        for (AbstractFunction func : _functions) {
//            value = func.calc(caster, target, skill, value);
//        }
//        return value;
//    }

    /**
     * Get array of all function, dont use for add/remove
     * @return
     */
    public AbstractFunction[] getFunctions() {
        return _functions;
    }
}

