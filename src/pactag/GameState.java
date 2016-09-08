/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pactag;

import java.awt.Graphics;

/**
 *
 * @author Josh
 */
public abstract class GameState {
	protected GameStateManager StateController;
	protected Renderer GameRender;
	public GameState(GameStateManager StateManager, Renderer Render){StateController = StateManager; GameRender = Render;}
	public abstract void Update();
	public abstract void Pause();
	public abstract void Resume();
	public abstract void Draw(Graphics g);
}
