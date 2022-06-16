using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class World : MonoBehaviour
{
    public GameObject playerPrefab;
    private EventProcessor _eventProcessor;
    public Dictionary<string, Entity> players = new Dictionary<string, Entity>();
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

            Entity player;
            if(players.TryGetValue(name, out player)) {
               players.Remove(name);
            }

            objects.Remove(id);

             _eventProcessor.QueueEvent(() => Object.Destroy(transform.gameObject));
        }
    }

    public void SpawnPlayer(NetworkIdentity identity, PlayerStatus player) {
        Debug.Log("New player");
        _eventProcessor.QueueEvent(() => InstantiatePlayer(identity, player));
    }

    public void InstantiatePlayer(NetworkIdentity identity, PlayerStatus status) {
        Debug.Log("Instantiate Player");
        GameObject go = (GameObject)Instantiate(playerPrefab, identity.GetPosition(), Quaternion.identity);
        NetworkTransform networkTransform = go.GetComponent<NetworkTransform>();
        networkTransform.SetIdentity(identity);
        Entity player = go.GetComponent<Entity>();
        player.Status = status;   

        players.Add(identity.GetName(), player);     
        objects.Add(identity.GetId(), networkTransform);  

        if(identity.IsOwned()) {
            go.GetComponent<PlayerController>().enabled = true;
            Camera.main.GetComponent<CameraController>().target = go.transform;
            Camera.main.GetComponent<CameraController>().enabled = true;
            Camera.main.GetComponent<InputManager>().SetCameraController(Camera.main.GetComponent<CameraController>());
            Camera.main.GetComponent<InputManager>().SetPlayerController(go.GetComponent<PlayerController>());
            Camera.main.GetComponent<InputManager>().enabled = true;
        } else {
            go.GetComponent<PlayerController>().enabled = false;
        }
        
        go.transform.name = identity.GetName();
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

    public void UpdateObject(int id, int animId, float value) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.SetAnimationProperty(animId, value));
        }
    }

    public void InflictDamageTo(int sender, int target, byte attackId, int value) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(target, out networkTransform)) {
            _eventProcessor.QueueEvent(() => {
                networkTransform.GetComponentInParent<Entity>().ApplyDamage(sender, attackId, value);
            });
        }
    }
}
