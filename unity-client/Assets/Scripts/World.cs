using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class World : MonoBehaviour
{
    public GameObject playerPrefab;
    private EventProcessor _eventProcessor;
    public Dictionary<string, Player> players = new Dictionary<string, Player>();
    public Dictionary<int, NetworkTransform> objects = new Dictionary<int, NetworkTransform>();
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

    public void RemoveObject(int id) {
        NetworkTransform transform;
        if(objects.TryGetValue(id, out transform)) {
            string name = transform.GetIdentity().GetName();

            Player player;
            if(players.TryGetValue(name, out player)) {
               players.Remove(name);
            }

            objects.Remove(id);

             _eventProcessor.QueueEvent(() => Object.Destroy(transform.gameObject));
        }
    }

    public void SpawnPlayer(NetworkIdentity identity, PlayerStatus player) {              
        _eventProcessor.QueueEvent(() => InstantiatePlayer(identity, player));
    }

    public void InstantiatePlayer(NetworkIdentity identity, PlayerStatus status) {
        GameObject go = (GameObject)Instantiate(playerPrefab, identity.GetPosition(), Quaternion.identity);
        NetworkTransform networkTransform = go.GetComponent<NetworkTransform>();
        networkTransform.SetIdentity(identity); 
        Player player = go.GetComponent<Player>();
        player.SetStatus(status);   

        players.Add(identity.GetName(), player);     
        objects.Add(identity.GetId(), networkTransform);  

        if(identity.IsOwned()) {
            go.GetComponent<PlayerController>().enabled = true;
            go.GetComponent<InputManager>().enabled = true;
            Camera.main.GetComponent<CameraController>().target = go.transform;
            Camera.main.GetComponent<CameraController>().enabled = true;
        } else {
            go.GetComponent<PlayerController>().enabled = false;
            go.GetComponent<InputManager>().enabled = false;
        }
        
        go.SetActive(true);  
    }

    public void UpdateObject(int id, Vector3 position) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.MoveTo(position));           
        }
    }

    public void UpdateObject(int id, float angle) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.RotateTo(angle));           
        }
    }
}
