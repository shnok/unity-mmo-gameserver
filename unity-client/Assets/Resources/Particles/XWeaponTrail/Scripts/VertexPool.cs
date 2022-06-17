//----------------------------------------------
//            Xffect Editor
// Copyright © 2012- Shallway Studio
// http://shallway.net
//----------------------------------------------
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Xft;

namespace Xft
{

    public class VertexPool
    {
        public class VertexSegment
        {
            public int VertStart;
            public int IndexStart;
            public int VertCount;
            public int IndexCount;
            public VertexPool Pool;

            public VertexSegment(int start, int count, int istart, int icount, VertexPool pool)
            {
                VertStart = start;
                VertCount = count;
                IndexCount = icount;
                IndexStart = istart;
                Pool = pool;
            }


            public void ClearIndices()
            {
                for (int i = IndexStart; i < IndexStart + IndexCount; i++)
                {
                    Pool.Indices[i] = 0;
                }

                Pool.IndiceChanged = true;
            }

        }

        public Vector3[] Vertices;
        public int[] Indices;
        public Vector2[] UVs;
        public Color[] Colors;

        public bool IndiceChanged;
        public bool ColorChanged;
        public bool UVChanged;
        public bool VertChanged;
        public bool UV2Changed;





        protected int VertexTotal;
        protected int VertexUsed;
        protected int IndexTotal = 0;
        protected int IndexUsed = 0;
        public bool FirstUpdate = true;

        protected bool VertCountChanged;


        public const int BlockSize = 108;

        public float BoundsScheduleTime = 1f;
        public float ElapsedTime = 0f;

        protected XWeaponTrail _owner;

        protected MeshFilter _meshFilter;

        protected Mesh _mesh2d;
        protected Material _material;

        public Mesh MyMesh {
            get {

                if (!_owner.UseWith2D) {
                    return _mesh2d;
                }
                else {
                    if (_meshFilter == null || _meshFilter.gameObject == null) {
                        return null;
                    }
                    return _meshFilter.sharedMesh;
                }
            }
        }

        public void RecalculateBounds()
        {
            MyMesh.RecalculateBounds();
        }


        void CreateMeshObj(XWeaponTrail owner, Material material) {
            GameObject obj = new GameObject("_XWeaponTrailMesh:" + "|material:" + material.name);
            obj.layer = owner.gameObject.layer;
            obj.AddComponent<MeshFilter>();
            obj.AddComponent<MeshRenderer>();

            obj.transform.position = Vector3.zero;
            obj.transform.rotation = Quaternion.identity;


            MeshRenderer Meshrenderer;
            _meshFilter = (MeshFilter)obj.GetComponent(typeof(MeshFilter));
            Meshrenderer = (MeshRenderer)obj.GetComponent(typeof(MeshRenderer));
#if UNITY_4_5 || UNITY_4_6 || UNITY_4_7 || UNITY_4_8
            Meshrenderer.castShadows = false;
#else
            Meshrenderer.shadowCastingMode = UnityEngine.Rendering.ShadowCastingMode.Off;
#endif
            Meshrenderer.receiveShadows = false;
            Meshrenderer.GetComponent<Renderer>().sharedMaterial = material;
            Meshrenderer.sortingLayerName = _owner.SortingLayerName;
            Meshrenderer.sortingOrder = _owner.SortingOrder;
            _meshFilter.sharedMesh = new Mesh();
        }

        public void Destroy() {

            if (!_owner.UseWith2D) {
                Mesh.DestroyImmediate(_mesh2d);
            }
            else {
                GameObject.Destroy(_meshFilter.gameObject);
            }
        }

        public VertexPool(Material material, XWeaponTrail owner)
        {
            VertexTotal = VertexUsed = 0;
            VertCountChanged = false;
            _owner = owner;
            if (owner.UseWith2D) {
                CreateMeshObj(owner, material);
            }
            else {
                _mesh2d = new Mesh();
            }
            _material = material;
            InitArrays();
            IndiceChanged = ColorChanged = UVChanged = UV2Changed = VertChanged = true;
        }


        public VertexSegment GetVertices(int vcount, int icount)
        {
            int vertNeed = 0;
            int indexNeed = 0;
            if (VertexUsed + vcount >= VertexTotal)
            {
                vertNeed = (vcount / BlockSize + 1) * BlockSize;
            }
            if (IndexUsed + icount >= IndexTotal)
            {
                indexNeed = (icount / BlockSize + 1) * BlockSize;
            }
            VertexUsed += vcount;
            IndexUsed += icount;
            if (vertNeed != 0 || indexNeed != 0)
            {
                EnlargeArrays(vertNeed, indexNeed);
                VertexTotal += vertNeed;
                IndexTotal += indexNeed;
            }

            VertexSegment ret = new VertexSegment(VertexUsed - vcount, vcount, IndexUsed - icount, icount, this);

            return ret;
        }


        protected void InitArrays()
        {
            Vertices = new Vector3[4];
            UVs = new Vector2[4];
            Colors = new Color[4];
            Indices = new int[6];
            VertexTotal = 4;
            IndexTotal = 6;
        }



        public void EnlargeArrays(int count, int icount)
        {
            Vector3[] tempVerts = Vertices;
            Vertices = new Vector3[Vertices.Length + count];
            tempVerts.CopyTo(Vertices, 0);

            Vector2[] tempUVs = UVs;
            UVs = new Vector2[UVs.Length + count];
            tempUVs.CopyTo(UVs, 0);

            Color[] tempColors = Colors;
            Colors = new Color[Colors.Length + count];
            tempColors.CopyTo(Colors, 0);

            int[] tempTris = Indices;
            Indices = new int[Indices.Length + icount];
            tempTris.CopyTo(Indices, 0);

            VertCountChanged = true;
            IndiceChanged = true;
            ColorChanged = true;
            UVChanged = true;
            VertChanged = true;
            UV2Changed = true;
        }




        public void LateUpdate()
        {
            if (VertCountChanged)
            {
                MyMesh.Clear();
            }

            // we assume the vertices are always changed.
            MyMesh.vertices = Vertices;
            if (UVChanged)
            {
                MyMesh.uv = UVs;
            }

            if (ColorChanged)
            {
                MyMesh.colors = Colors;
            }

            if (IndiceChanged)
            {
                MyMesh.triangles = Indices;
            }

            ElapsedTime += Time.deltaTime;
            if (ElapsedTime > BoundsScheduleTime || FirstUpdate)
            {
                RecalculateBounds();
                ElapsedTime = 0f;
            }

            if (ElapsedTime > BoundsScheduleTime)
                FirstUpdate = false;

            VertCountChanged = false;
            IndiceChanged = false;
            ColorChanged = false;
            UVChanged = false;
            UV2Changed = false;
            VertChanged = false;


            if (_owner.UseWith2D) {

            }
            else {
                //Matrix4x4 matrix = Matrix4x4.TRS(Vector3.zero, Quaternion.identity, Vector3.one);
                Graphics.DrawMesh(MyMesh, Matrix4x4.identity, _material, _owner.gameObject.layer, null, 0, null, false, false);
            }
        }
    }
}