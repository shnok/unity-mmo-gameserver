using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using System.Collections;

public class World : MonoBehaviour
{
    public GameObject playerPrefab;
    private EventProcessor _eventProcessor;
    public List<Player> players = new List<Player>();
    public List<NetworkTransform> objects = new List<NetworkTransform>();
    public static World _instance;
    public static World GetInstance() {
        return _instance;
    }

    void Awake() {
        if (_instance == null) {
            _instance = this;
        }
        
        playerPrefab = Resources.Load("Prefab/Player") as GameObject;
        _eventProcessor = gameObject.GetComponent<EventProcessor>();
    }

    void Start() {
        
    }

    void Update() {
        
    }

    public void SpawnPlayer(NetworkIdentity identity, PlayerStatus player) {              
        _eventProcessor.QueueEvent(() => InstantiatePlayer(identity, player));
    }
    public void InstantiatePlayer(NetworkIdentity identity, PlayerStatus player) {
        GameObject go = (GameObject)Instantiate(playerPrefab, new Vector3(), Quaternion.identity);
        NetworkTransform nt = go.GetComponent<NetworkTransform>();
        nt.SetIdentity(identity); 
        Player p = go.GetComponent<Player>();
        p.SetStatus(player);   

        players.Add(p);     
        objects.Add(nt);  
        
        go.SetActive(true);  
    }
}
