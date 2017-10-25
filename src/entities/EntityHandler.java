package entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import main.GameWindow;
import terrain.Tile;
import terrain.TileHandler;

public class EntityHandler {

	private ArrayList<Entity> entities;

	public EntityHandler() {
		entities = new ArrayList<Entity>();
	}

	public void tick(TileHandler t) {
		for (Entity e : entities) {
			e.tick(this);
		}
	}

	public static double getAngle(Entity a, Entity b) {

		double dx = a.getAbsX() - b.getAbsX();
		double dy = a.getAbsY() - b.getAbsY();

		if (b instanceof Player) {
			dx -= b.getWidth()/2;
			dy -= b.getWidth()/2;
		}
		double angle = Math.atan(dy / dx);
		if (dx < 0)
			angle += Math.PI;
		return angle;
	}

	public void addEntity(Entity e) {
		entities.add(e);
	}

	public void addEntities(ArrayList<Entity> e) {
		if (e != null) {
			for (Entity a : e) {
				entities.add(a);
			}
		}
	}

	// same as addEntities() but also updates the texture of each one
	// only for when tiles are being reloaded
	public void addEntitiesFromSave(ArrayList<Entity> e) {
		if (e != null) {
			for (Entity a : e) {
				a.updateTexture();
				entities.add(a);
			}
		}
	}

	public void renderEntitiesAt(GameWindow w, Graphics2D g, Tile t, double playerH, Player player, double rotation) {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (t.inBorder(e.getAbsX(), e.getAbsY())) {
				e.draw(w, g, t, player, rotation, playerH - t.getH());
			}
		}
	}

	public ArrayList<Entity> getEntitiesInTile(Tile t) {
		ArrayList<Entity> e = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			if (!(entities.get(i) instanceof Player)) {
				if (t.inBorder(entities.get(i).getAbsX(), entities.get(i).getAbsY())) {
					e.add(entities.get(i));
					entities.remove(i);
				}
			}
		}
		return e;
	}

	public Entity randomEntity() { // FIXME get list of all Entities and use
									// those
		return null;
	}

	public Entity getNearestEntity(Entity e) {
		Entity closest = null;
		for (int i = 0; i < entities.size(); i++) {
			if (closest == null && entities.get(i) != e) {
				closest = entities.get(i);
			} else if (entities.get(i) != e && dist(e, closest) > dist(e, entities.get(i))) {
				closest = entities.get(i);
			}
		}
		return closest;
	}

	public Entity getNearestEntity(Entity e, String name) {
		Entity closest = null;
		for (int i = 0; i < entities.size(); i++) {
			if (closest == null && entities.get(i) != e && entities.get(i).getName().equals(name)) {
				closest = entities.get(i);
			}
			if (closest != null && entities.get(i) != e && dist(e, closest) > dist(e, entities.get(i))
					&& entities.get(i).getName().equals(name)) {
				closest = entities.get(i);
			}
		}
		return closest;
	}

	private double dist(Entity e1, Entity e2) {
		double dx = (e1.getAbsX() - e2.getAbsX()) * (e1.getAbsX() - e2.getAbsX());
		double dy = (e1.getAbsY() - e2.getAbsY()) * (e1.getAbsY() - e2.getAbsY());
		return Math.sqrt(dx + dy);
	}

	public int getEntityCount() {
		return entities.size();
	}

	public void saveEntities(ArrayList<Entity> entitiesToSave) {
		try {
			FileOutputStream f = new FileOutputStream(new File("myObjects.txt"));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			o.writeObject(entitiesToSave);

			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Entity> loadEntities(Tile t) {

		try {
			FileInputStream fi = new FileInputStream(new File("myObjects.txt"));
			ObjectInputStream oi = new ObjectInputStream(fi);

			ArrayList<Entity> entities = (ArrayList<Entity>) oi.readObject();

			oi.close();
			fi.close();
			return entities;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
