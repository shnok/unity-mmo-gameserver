using UnityEngine;

public class NetworkTransform : MonoBehaviour {
    public NetworkIdentity identity;
    public void SetIdentity(NetworkIdentity i) {
       identity = i; 
    }

    
}