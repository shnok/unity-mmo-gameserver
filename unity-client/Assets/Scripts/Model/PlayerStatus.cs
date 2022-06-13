using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class PlayerStatus {
    public int hp;
    public int maxHp;
    public int stamina;
    public int maxStamina;
    public int level;

    public PlayerStatus() {}

    public void SetLevel(int i) {
        level = i;
    }
    public void SetHp(int i) {
        hp = i;
    }
    public void SetMaxHp(int i) {
        maxHp = i;
    }
    public void SetStamina(int i) {
        stamina = i;
    }
    public void SetMaxStamina(int i) {
        maxStamina = i;
    }
}