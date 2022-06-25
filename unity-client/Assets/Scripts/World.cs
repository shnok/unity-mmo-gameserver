using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class World : MonoBehaviour
{
    public GameObject playerPrefab;
    public GameObject npcPrefab;
    public GameObject labelPrefab;
    private EventProcessor _eventProcessor;
    public Dictionary<int, Entity> players = new Dictionary<int, Entity>();
    public Dictionary<int, Entity> npcs = new Dictionary<int, Entity>();
    public Dictionary<int, NetworkTransform> objects = new Dictionary<int, NetworkTransform>();
    public GameObject mainPlayer;

    public static World _instance;
    public static World GetInstance() {
        return _instance;
    }

    void Awake() {
        if (_instance == null) {
            _instance = this;
        }
        
        playerPrefab = Resources.Load("Prefab/Player") as GameObject;
        npcPrefab = Resources.Load("Prefab/Monster") as GameObject;
        labelPrefab = Resources.Load("Prefab/EntityLabel") as GameObject;
        _eventProcessor = gameObject.GetComponent<EventProcessor>();
    }

    public void RemoveObject(int id) {
        NetworkTransform transform;
        if(objects.TryGetValue(id, out transform)) {
            players.Remove(id);
            npcs.Remove(id);
            objects.Remove(id);

             _eventProcessor.QueueEvent(() => Object.Destroy(transform.gameObject));
        }
    }

    public void SpawnPlayer(NetworkIdentity identity, PlayerStatus player) {
        _eventProcessor.QueueEvent(() => InstantiatePlayer(identity, player));
    }

    public void SpawnNpc(NetworkIdentity identity, NpcStatus npc) {
        _eventProcessor.QueueEvent(() => InstantiateNpc(identity, npc));
    }

    public void InstantiatePlayer(NetworkIdentity identity, PlayerStatus status) {
        GameObject go = (GameObject)Instantiate(playerPrefab, identity.Position, Quaternion.identity);
        NetworkTransform networkTransform = go.GetComponent<NetworkTransform>();
        networkTransform.SetIdentity(identity);
        Entity player = go.GetComponent<Entity>();
        player.Status = status;   

        players.Add(identity.Id, player);     
        objects.Add(identity.Id, networkTransform);  

        if(identity.Owned) {
            go.GetComponent<PlayerController>().enabled = true;
            go.GetComponent<HitDetection>().enabled = true;

            Camera.main.GetComponent<CameraController>().target = go.transform;
            Camera.main.GetComponent<CameraController>().enabled = true;
            Camera.main.GetComponent<InputManager>().SetCameraController(Camera.main.GetComponent<CameraController>());
            Camera.main.GetComponent<InputManager>().SetPlayerController(go.GetComponent<PlayerController>());
            Camera.main.GetComponent<InputManager>().enabled = true;
            mainPlayer = go;
        } else {
            go.GetComponent<PlayerController>().enabled = false;
        }
        
        go.transform.name = identity.Name;
        go.SetActive(true);

        InstantiateLabel(go);
    }

    public void InstantiateNpc(NetworkIdentity identity, NpcStatus status) {
        /* Should check at npc id to load right npc name and model */
        GameObject go = (GameObject)Instantiate(npcPrefab, identity.Position, Quaternion.identity);
        NetworkTransform networkTransform = go.GetComponent<NetworkTransform>();
        identity.Name = "Dummy";
        networkTransform.SetIdentity(identity);
        Entity npc = go.GetComponent<Entity>();
        npc.Status = status;

        npcs.Add(identity.Id, npc);
        objects.Add(identity.Id, networkTransform);

        go.SetActive(true);

        InstantiateLabel(go);
    }

    public void InstantiateLabel(GameObject target) {
        GameObject go = (GameObject)Instantiate(labelPrefab, Vector3.zero, Quaternion.identity);  
        go.GetComponent<Label>().SetTarget(target);
        go.GetComponent<Label>().enabled = true;
        go.transform.SetParent(GameObject.Find("UI").transform);
        go.SetActive(true);
    }

    public void UpdateObjectPosition(int id, Vector3 position, bool lookAt) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.MoveTo(position));
            if(lookAt) {
                _eventProcessor.QueueEvent(() => networkTransform.LookAt(position));
            }
        }
    }

    public void UpdateObjectRotation(int id, float angle) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.RotateTo(angle));           
        }
    }

    public void UpdateObjectAnimation(int id, int animId, float value) {
        NetworkTransform networkTransform;
        if(objects.TryGetValue(id, out networkTransform)) {
            _eventProcessor.QueueEvent(() => networkTransform.SetAnimationProperty(animId, value));
        }
    }

    public void InflictDamageTo(int sender, int target, byte attackId, int value) {
        NetworkTransform senderNetworkTransform;
        NetworkTransform targetNetworkTransform;
        if(objects.TryGetValue(sender, out senderNetworkTransform)) {
            if(objects.TryGetValue(target, out targetNetworkTransform)) {
                _eventProcessor.QueueEvent(() => {
                    //networkTransform.GetComponentInParent<Entity>().ApplyDamage(sender, attackId, value);
                    Combat.GetInstance().ApplyDamage(senderNetworkTransform.transform, targetNetworkTransform.transform, attackId, value);
                });
            }
        }
    }
}
