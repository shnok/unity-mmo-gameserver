using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HitDetection : MonoBehaviour
{
    public Vector3 castCenter;
    public Vector3 castHalfExtents;
    public Vector3 castDirection;
    public float castDistance;
    public LayerMask castLayerMask;
    public RaycastHit[] hits;
    public List<Transform> targets;

    void Update() {
        hits = Physics.BoxCastAll(transform.position + castCenter, castHalfExtents, transform.forward + castDirection, transform.rotation, castDistance, castLayerMask);
        List<Transform> ts = new List<Transform>();
        for(int i = 0; i<hits.Length; i++) {
            if(hits[i].transform != transform)
                ts.Add(hits[i].transform);
        }
        targets = ts;
    }

    void OnDrawGizmos() {
        Gizmos.DrawWireCube(transform.position + castCenter + transform.forward * castDistance, castHalfExtents * 2);
    }

    private void SendAttack(AttackType attackType) {
        foreach(Transform t in targets) {
            WorldCombat.GetInstance().Attack(t, attackType);
        }
    }

    public void SwordAttack() {
        SendAttack(AttackType.AutoAttack);
    }
}
