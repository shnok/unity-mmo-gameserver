using UnityEngine;
public class Player : MonoBehaviour {
    public PlayerStatus status;
    private NetworkIdentity identity;

    public void SetStatus(PlayerStatus s) {
        status = s;
    }

    void Update() {

    }
}