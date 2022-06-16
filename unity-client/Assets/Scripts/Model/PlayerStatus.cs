using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class PlayerStatus : Status {
    [SerializeField]
    private int stamina;
    [SerializeField]
    private int maxStamina;
    public int Stamina { get => stamina; set => stamina = value; }
    public int MaxStamina { get => maxStamina; set => maxStamina = value; }

    public PlayerStatus() {}
}