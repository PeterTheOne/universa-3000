package cogaenfix;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.MouseInputListener;

import org.cogaen.core.Core;
import org.cogaen.core.UpdateableService;
import org.cogaen.event.Event;
import org.cogaen.event.EventManager;
import org.cogaen.input.KeyPressedEvent;
import org.cogaen.input.KeyReleasedEvent;
import org.cogaen.input.KeyTypedEvent;
import org.cogaen.input.MousePressedEvent;
import org.cogaen.input.WheelEvent;


public class InputManager implements UpdateableService, KeyListener, MouseInputListener, MouseWheelListener {

	public static final String NAME = "cogaen.inputmanager";
	
	private static final int NUM_KEY_CODES = 600;
	private KeyState[] keyStates = new KeyState[NUM_KEY_CODES];
	private EventManager evtMngr;
	private Core core;
	
	public InputManager(Component comp) {
		comp.addKeyListener(this);
		comp.addMouseListener(this);
		comp.addMouseWheelListener(this);
		comp.addMouseMotionListener(this);
		comp.setFocusTraversalKeysEnabled(false);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialize(Core core) {
		this.core = core;
		this.evtMngr = EventManager.getInstance( this.core );
	}
	
	public static InputManager getInstance(Core core) {
		return (InputManager) core.getService(NAME);
	}

	public synchronized KeyState getKeyState(int keyCode) {
		
		if (keyCode > NUM_KEY_CODES || keyCode < 0) {
			throw new IllegalArgumentException("key code '" + keyCode + "' is not valid");
		}
		
		KeyState keyState = this.keyStates[keyCode];
		if (keyState == null) {
			keyState = new KeyState();
			this.keyStates[keyCode] = keyState;
		}
		
		return keyState;
	}
	
	private synchronized KeyState getKeyState(KeyEvent e) {
		int code = e.getKeyCode();
		if (code < this.keyStates.length) {
			return this.keyStates[code];
		} else {
			return null;
		}
	}
	
	@Override
	public synchronized void keyPressed(KeyEvent e) {
		KeyState keyState = getKeyState(e);
		if (keyState != null) {
			//TODO: why not public?
			keyState.setPressed(true);
		}
		this.evtMngr.enqueueEvent( new KeyPressedEvent(e.getKeyCode()) ); 
		e.consume();
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		KeyState keyState = getKeyState(e);
		if (keyState != null) {
			keyState.setPressed(false);
		}
		this.evtMngr.enqueueEvent( new KeyReleasedEvent(e.getKeyCode()) ); 
		e.consume();
	}

	@Override
	public synchronized void keyTyped(KeyEvent e) {
		this.evtMngr.enqueueEvent( new KeyTypedEvent(e.getKeyChar()) ); 
		e.consume();
	}

	@Override
	public synchronized void update() {	
		//empty
	}

	@Override
	public synchronized void mouseClicked(MouseEvent event) {
		event.consume();		
	}

	@Override
	public synchronized void mouseEntered(MouseEvent event) {
		event.consume();
	}

	@Override
	public synchronized void mouseExited(MouseEvent event) {
		event.consume();
	}

	@Override
	public synchronized void mousePressed(MouseEvent e) {
		this.evtMngr.enqueueEvent( new MousePressedEvent(e.getButton(), e.getX(), e.getY()) );
		e.consume();
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		this.evtMngr.enqueueEvent( new MouseReleasedEvent(e.getButton(), e.getX(), e.getY()) );
		e.consume();
	}

	@Override
	public synchronized void mouseWheelMoved(MouseWheelEvent e) {
		this.evtMngr.enqueueEvent( new WheelEvent(e.getWheelRotation()) );
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.evtMngr.enqueueEvent( new MouseMovedEvent(e.getX(), e.getY()) );
		e.consume();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.evtMngr.enqueueEvent( new MouseDraggedEvent(e.getButton(), e.getX(), e.getY()) );
		e.consume();
	}
}
