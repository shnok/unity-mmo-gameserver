using UnityEngine;
public class Player : MonoBehaviour {
    public PlayerStatus status;

    void Awake() {
    }

    void Update() {

    }
    public void SetStatus(PlayerStatus s) {
        status = s;
    }

}