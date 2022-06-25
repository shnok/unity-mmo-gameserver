using UnityEngine;
public class Entity : MonoBehaviour {
    [SerializeReference]
    public Status status;

    [SerializeReference]
    private NetworkIdentity _identity;

    public Status Status { get => status; set => status = value; }

    public NetworkIdentity Identity { get => _identity; set => _identity = value; }

    /* Called when ApplyDamage packet is received */
    public void ApplyDamage(byte attackId, int value) {
        status.Hp--;
    }

    /* Notify server that entity got attacked */
    public void Attack(AttackType attackType) {
        ClientPacketHandler.GetInstance().InflictAttack(_identity.Id, attackType);
    }
}