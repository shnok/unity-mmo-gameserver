using UnityEngine;
public class Entity : MonoBehaviour {
    [SerializeReference]
    public Status status;

    private NetworkTransform _network;
    public Status Status { get => status; set => status = value; }

    /* Called when ApplyDamage packet is received */
    public void ApplyDamage(byte attackId, int value) {
        status.Hp--;
    }

    /* Notify server that entity got attacked */
    public void Attack(AttackType attackType) {
        ClientPacketHandler.GetInstance().InflictAttack(_network.GetIdentity().GetId(), attackType);
    }

    void Start() {
        _network = GetComponent<NetworkTransform>(); 
    }
}