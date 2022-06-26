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
    public Dictionary<int, Entity> objects = new Dictionary<int, Entity>();
    public GameObject mainPlayer;
    public LayerMask groundMask;

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
        Entity transform;
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
        identity.SetPosY(getGroundHeight(identity.Position));
        GameObject go = (GameObject)Instantiate(playerPrefab, identity.Position, Quaternion.identity);
        Entity player = go.GetComponent<Entity>();
        player.Status = status;
        player.Identity = identity;

        players.Add(identity.Id, player);     
        objects.Add(identity.Id, player);  

        if(identity.Owned) {
            go.GetComponent<PlayerController>().enabled = true;
            go.GetComponent<HitDetection>().enabled = true;
            go.GetComponent<NetworkTransformReceive>().enabled = false;
            go.GetComponent<NetworkTransformShare>().enabled = true;
            Camera.main.GetComponent<CameraController>().target = go.transform;
            Camera.main.GetComponent<CameraController>().enabled = true;
            Camera.main.GetComponent<InputManager>().SetCameraController(Camera.main.GetComponent<CameraController>());
            Camera.main.GetComponent<InputManager>().SetPlayerController(go.GetComponent<PlayerController>());
            Camera.main.GetComponent<InputManager>().enabled = true;
            mainPlayer = go;
        } else {
            go.GetComponent<PlayerController>().enabled = false;
            go.GetComponent<NetworkTransformReceive>().enabled = true;
            go.GetComponent<NetworkTransformShare>().enabled = false;
        }
        
        go.transform.name = identity.Name;
        go.SetActive(true);

        InstantiateLabel(go);
    }

    public void InstantiateNpc(NetworkIdentity identity, NpcStatus status) {
        /* Should check at npc id to load right npc name and model */
        identity.SetPosY(getGroundHeight(identity.Position));
        GameObject go = (GameObject)Instantiate(npcPrefab, identity.Position, Quaternion.identity);
        identity.Name = "Dummy";
        Entity npc = go.GetComponent<Entity>();
        npc.Status = status;
        npc.Identity = identity;

        npcs.Add(identity.Id, npc);
        objects.Add(identity.Id, npc);

        go.SetActive(true);

        InstantiateLabel(go);
    }

    public float getGroundHeight(Vector3 pos) {
        RaycastHit hit;
        if(Physics.Raycast(pos + Vector3.up, Vector3.down, out hit, 1.1f, groundMask)) {
            return hit.point.y;
        }

        return pos.y;
    }

    public void InstantiateLabel(GameObject target) {
        GameObject go = (GameObject)Instantiate(labelPrefab, Vector3.zero, Quaternion.identity);  
        go.GetComponent<Label>().SetTarget(target);
        go.GetComponent<Label>().enabled = true;
        go.transform.SetParent(GameObject.Find("UI").transform);
        go.SetActive(true);
    }

    public void UpdateObjectPosition(int id, Vector3 position) {
        Entity e;
        if(objects.TryGetValue(id, out e)) {
            _eventProcessor.QueueEvent(() => e.GetComponent<NetworkTransformReceive>().SetNewPosition(position));
        }
    }

    public void UpdateObjectDestination(int id, Vector3 position) {
        Entity e;
        if(objects.TryGetValue(id, out e)) {
            _eventProcessor.QueueEvent(() => e.GetComponent<NetworkTransformReceive>().SetDestination(position));
            _eventProcessor.QueueEvent(() => e.GetComponent<NetworkTransformReceive>().LookAt(position));
        }
    }

    public void UpdateObjectRotation(int id, float angle) {
        Entity e;
        if(objects.TryGetValue(id, out e)) {
            _eventProcessor.QueueEvent(() => e.GetComponent<NetworkTransformReceive>().RotateTo(angle));           
        }
    }

    public void UpdateObjectAnimation(int id, int animId, float value) {
        Entity e;
        if(objects.TryGetValue(id, out e)) {
            _eventProcessor.QueueEvent(() => e.GetComponent<NetworkTransformReceive>().SetAnimationProperty(animId, value));
        }
    }

    public void InflictDamageTo(int sender, int target, byte attackId, int value) {
        Entity senderEntity;
        Entity targetEntity;
        if(objects.TryGetValue(sender, out senderEntity)) {
            if(objects.TryGetValue(target, out targetEntity)) {
                _eventProcessor.QueueEvent(() => {
                    //networkTransform.GetComponentInParent<Entity>().ApplyDamage(sender, attackId, value);
                    WorldCombat.GetInstance().ApplyDamage(senderEntity.transform, targetEntity.transform, attackId, value);
                });
            }
        }
    }
}
