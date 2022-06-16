using UnityEngine;
public class Entity : MonoBehaviour {
    [SerializeReference]
    public Status status;

    private NetworkTransform _network;
    public Status Status { get => status; set => status = value; }

    /* Called when ApplyDamage packet is received */
    public void ApplyDamage(int sender, byte attackId, int value) {
        Debug.Log(_network.GetIdentity().GetName() + " received " + value + " damage (s) from " + sender);
        Debug.Log(transform.name);
    }

    /* Notify server that entity got attacked */
    public void Attack(int damage) {
        ClientPacketHandler.GetInstance().InflictAttack(_network.GetIdentity().GetId(), 0, damage);
    }

    void Start() {
        _network = GetComponent<NetworkTransform>(); 
    }
}