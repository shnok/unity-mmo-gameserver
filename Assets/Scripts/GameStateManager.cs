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
        SceneManager.sceneLoaded += OnSceneLoaded;
    }

    private void HandleEvent() {
        _eventProcessor.QueueEvent(HandleOnStateChange);
    }

    private void HandleOnStateChange() {
        if(_state == GameState.MENU) {
            SceneManager.LoadScene("MenuScene");           
        }

        if(_state == GameState.CONNECTED) {
            SceneManager.LoadScene("GameScene");
        }
    }

    void OnSceneLoaded(Scene scene, LoadSceneMode mode) {
        SceneManager.SetActiveScene(scene);
    }
}
