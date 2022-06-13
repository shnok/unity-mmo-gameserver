using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public enum GameState { MENU, CONNECTED }
public delegate void OnStateChangeHandler();

public class GameStateManager : MonoBehaviour
{
    public static event OnStateChangeHandler StateChanged;
    private static GameState _state = GameState.MENU;
    private EventProcessor _eventProcessor;
    private AsyncOperation asyncLoad;
    public static void SetState(GameState ns) {
        _state = ns;
        StateChanged?.Invoke();
    }

    public static GameState GetState() {
        return _state;
    }

    void Awake() {
        _eventProcessor = gameObject.GetComponent<EventProcessor>();
        StateChanged += HandleEvent;
       // SceneManager.sceneLoaded += OnSceneLoaded;
       // SceneManager.activeSceneChanged += OnChangedActiveScene;
    }

    private void HandleEvent() {
        _eventProcessor.QueueEvent(HandleOnStateChange);
    }

    private void HandleOnStateChange() {
        if(_state == GameState.MENU) {
            StartCoroutine(LoadAsyncScene("MenuScene"));  
            //asyncLoad.allowSceneActivation = false;                          
        }
        if(_state == GameState.CONNECTED) {
           StartCoroutine(LoadAsyncScene("GameScene"));  
           //asyncLoad.allowSceneActivation = false; 
        }    
    }

    IEnumerator LoadAsyncScene(string scene) {
        AsyncOperation asyncLoad = SceneManager.LoadSceneAsync(scene);

        // Wait until the asynchronous scene fully loads
        while (!asyncLoad.isDone) {
            yield return null;
        }

        if(_state == GameState.CONNECTED) {
            DefaultClient.GetInstance().ClientReady();
        }

        if(_state == GameState.MENU) {
            DefaultClient.GetInstance().OnDisconnectReady();
        }               
    } 
}



    /*void OnSceneLoaded(Scene scene, LoadSceneMode mode) {
        Debug.Log("scene loaded:" + scene);
        if(asyncLoad != null) {
            
            asyncLoad.allowSceneActivation = true;
        }
        
        SceneManager.SetActiveScene(scene);
    }

    void OnChangedActiveScene(Scene scene, Scene next) {
        Debug.Log("Scenes: " + scene.name + ", " + next.name);
        if(next.name == "GameScene") {
            DefaultClient.GetInstance().ClientReady();
        }
       
    }*/