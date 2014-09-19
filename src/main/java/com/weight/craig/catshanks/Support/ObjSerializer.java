package com.weight.craig.catshanks.Support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.weight.craig.catshanks.GameObjects.Particles.CollisionParticle;
import com.weight.craig.catshanks.GameObjects.PowerUps.CrabPowerUp;
import com.weight.craig.catshanks.BaseObjects.Enemy;
import com.weight.craig.catshanks.GameObjects.Particles.ExplosionParticle;
import com.weight.craig.catshanks.GameObjects.Enemies.GarbageTruck;
import com.weight.craig.catshanks.GameObjects.PowerUps.LifePowerUp;
import com.weight.craig.catshanks.BaseObjects.NPC;
import com.weight.craig.catshanks.BaseObjects.Particle;
import com.weight.craig.catshanks.GameObjects.PowerUps.PepsiPowerUp;
import com.weight.craig.catshanks.GameObjects.Players.Player;
import com.weight.craig.catshanks.GameObjects.PowerUps.PoptartPowerUp;
import com.weight.craig.catshanks.BaseObjects.PowerUp;
import com.weight.craig.catshanks.BaseObjects.Projectile;
import com.weight.craig.catshanks.GameObjects.Enemies.Robot;
import com.weight.craig.catshanks.GameObjects.Enemies.SpaceCrab;
import com.weight.craig.catshanks.GameObjects.Enemies.SpaceSubmarine;

import java.lang.reflect.Type;

/**
 * Created by Craig on 7/22/2014.
 */
public class ObjSerializer {
    public static final String oPlayer="Player";
    public static final String oFriendlyProjectile="Projectile-Friendly";
    public static final String oEnemyProjectile="Projectile-Enemy";
    public static final String oScore="Game-Score";
    public static final String oNextUpdate="Game-NextUpdate";
    public static final String oRobot="Enemy-Robot";
    public static final String oSpaceCrab="Enemy-SpaceCrab";
    public static final String oGarbageTruck="Enemy-GarbageTruck";
    public static final String oSpaceSubmarine="Enemy-SpaceSubmarine";
    public static final String oExplosionParticle="Particle-Explosion";
    public static final String oCollisionParticle="Particle-Collision";
    public static final String oPepsiPowerUp="PowerUp-Pepsi";
    public static final String oLifePowerUp="PowerUp-Life";
    public static final String oCrabPowerUp="PowerUp-Crab";
    public static final String oPoptartPowerUp="PowerUp-Poptart";

    private Gson gson=null;
    public Gson getGson(){
        if(gson==null){
            GsonBuilder gsonBuilder=new GsonBuilder();

            //-----------------------------------------
            //Register player Serializer/De-Serializer
            gsonBuilder.registerTypeAdapter(Player.class, new ObjSerializer.PlayerSerializer());

            //Register Enemy Serializers/De-Serializers
            gsonBuilder.registerTypeAdapter(Robot.class, new ObjSerializer.RobotSerializer());
            gsonBuilder.registerTypeAdapter(SpaceCrab.class, new ObjSerializer.SpaceCrabSerializer());
            gsonBuilder.registerTypeAdapter(SpaceSubmarine.class, new ObjSerializer.SpaceSubmarineSerializer());
            gsonBuilder.registerTypeAdapter(GarbageTruck.class, new ObjSerializer.GarbageTruckSerializer());

            //Register Projectile Serializer/De-Serializer
            gsonBuilder.registerTypeAdapter(Projectile.class, new ObjSerializer.ProjectileSerializer());

            //Register PowerUp Serializers/De-Serializers
            gsonBuilder.registerTypeAdapter(PepsiPowerUp.class, new ObjSerializer.PepsiPowerUpSerializer());
            gsonBuilder.registerTypeAdapter(LifePowerUp.class, new ObjSerializer.LifePowerUpSerializer());
            gsonBuilder.registerTypeAdapter(CrabPowerUp.class, new ObjSerializer.CrabPowerUpSerializer());
            gsonBuilder.registerTypeAdapter(PoptartPowerUp.class, new ObjSerializer.PoptartPowerUpSerializer());

            //Register Particle Serializers/De-Serializers
            gsonBuilder.registerTypeAdapter(CollisionParticle.class, new ObjSerializer.CollisionParticleSerializer());
            gsonBuilder.registerTypeAdapter(ExplosionParticle.class, new ObjSerializer.ExplosionParticleSerializer());
            //---------------------------------------------

            gson = gsonBuilder.create();
        }
        return gson;
    }

    private class EnemySerializer implements JsonSerializer<Enemy>{
        @Override
        public JsonElement serialize(Enemy enemy, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new JsonObject();
            result.add("X",new JsonPrimitive(enemy.getX()));
            result.add("Y",new JsonPrimitive(enemy.getY()));
            result.add("Angle",new JsonPrimitive(enemy.getAngle()));
            result.add("AngularChange",new JsonPrimitive(enemy.getAngularChange()));
            result.add("Speed",new JsonPrimitive(enemy.getSpeed()));
            result.add("HP",new JsonPrimitive(enemy.getHp()));
            result.add("AutoAim", new JsonPrimitive(enemy.getAutoAim()));
            if(enemy.getTimer()<enemy.getNextUpdate()) {
                result.add("NextUpdate",new JsonPrimitive(enemy.getNextUpdate()-enemy.getTimer()));
            }
            else result.add("NextUpdate",new JsonPrimitive(0.00f));
            result.add("TimeToFire", new JsonPrimitive(enemy.getTimeToFire()));
            return result;
        }
    }

    private class GarbageTruckSerializer implements JsonSerializer<GarbageTruck>,JsonDeserializer<GarbageTruck>{
        @Override
        public JsonElement serialize(GarbageTruck garbageTruck, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new EnemySerializer().serialize(garbageTruck,type,jsonSerializationContext).getAsJsonObject();
            result.add("removalTimer", new JsonPrimitive(garbageTruck.getTimeToRemoval()));
            result.add("removalTime", new JsonPrimitive(garbageTruck.getRemovalTime()));
            result.add("atCenter", new JsonPrimitive(garbageTruck.isAtCenter()));
            result.add("Type",new JsonPrimitive("GarbageTruck"));
            return result;
        }

        @Override
        public GarbageTruck deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new GarbageTruck(
                    obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("HP").getAsInt(),
                    obj.get("TimeToFire").getAsFloat(),
                    obj.get("removalTimer").getAsFloat(),
                    obj.get("removalTime").getAsFloat(),
                    obj.get("atCenter").getAsBoolean(),
                    obj.get("NextUpdate").getAsFloat());
        }
    }

    private class SpaceSubmarineSerializer implements JsonSerializer<SpaceSubmarine>,JsonDeserializer<SpaceSubmarine>{
        @Override
        public JsonElement serialize(SpaceSubmarine spaceSubmarine, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new EnemySerializer().serialize(spaceSubmarine,type,jsonSerializationContext).getAsJsonObject();
            result.add("Type",new JsonPrimitive("SpaceSubmarine"));
            return result;
        }

        @Override
        public SpaceSubmarine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new SpaceSubmarine(
                    obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("HP").getAsInt(),
                    obj.get("TimeToFire").getAsFloat());
        }
    }

    private class SpaceCrabSerializer implements JsonSerializer<SpaceCrab>,JsonDeserializer<SpaceCrab>{
        @Override
        public JsonElement serialize(SpaceCrab spaceCrab, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new EnemySerializer().serialize(spaceCrab,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("SpaceCrab"));
            return result;
        }

        @Override
        public SpaceCrab deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new SpaceCrab(
                    obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("HP").getAsInt(),
                    obj.get("TimeToFire").getAsFloat());
        }
    }

    private class RobotSerializer implements JsonSerializer<Robot>,JsonDeserializer<Robot>{
        @Override
        public JsonElement serialize(Robot robot, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new EnemySerializer().serialize(robot,null,null).getAsJsonObject();
            result.add("WaveMovement",new JsonPrimitive(robot.getInWaveFormation()));
            result.add("Type",new JsonPrimitive("Robot"));
            return result;
        }

        @Override
        public Robot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new Robot(
                    obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("AngularChange").getAsFloat(),
                    obj.get("WaveMovement").getAsBoolean(),
                    obj.get("Speed").getAsInt(),
                    obj.get("HP").getAsInt(),
                    obj.get("TimeToFire").getAsFloat());

        }
    }


    private static class NpcSerializer implements JsonSerializer<NPC>{
        @Override
        public JsonElement serialize(NPC npc, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new JsonObject();
            result.add("X",new JsonPrimitive(npc.getX()));
            result.add("Y",new JsonPrimitive(npc.getY()));
            result.add("Angle",new JsonPrimitive(npc.getAngle()));
            result.add("AngularChange",new JsonPrimitive(npc.getAngularChange()));
            result.add("Speed",new JsonPrimitive(npc.getSpeed()));
            return result;
        }
    }

    private class ProjectileSerializer implements JsonSerializer<Projectile>,JsonDeserializer<Projectile>{
        @Override
        public JsonElement serialize(Projectile projectile, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new NpcSerializer().serialize(projectile,null,null).getAsJsonObject();
            result.add("WeaponID",new JsonPrimitive(projectile.getWeaponId()));
            result.add("WeaponStrength", new JsonPrimitive(projectile.getStrength()));
            result.add("DestroyOnCollide", new JsonPrimitive(projectile.getDestroyOnCollide()));
            result.add("TimeTilCollidable",new JsonPrimitive(projectile.getTimeTilCollidable()));
            result.add("Type",new JsonPrimitive("Projectile"));
            return result;
        }

        @Override
        public Projectile deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new Projectile(  obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("WeaponStrength").getAsInt(),
                    obj.get("DestroyOnCollide").getAsBoolean(),
                    obj.get("TimeTilCollidable").getAsFloat(),
                    obj.get("WeaponID").getAsString());
        }
    }


    private class PowerUpSerializer implements JsonSerializer<PowerUp>{
        @Override
        public JsonElement serialize(PowerUp powerUp, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new NpcSerializer().serialize(powerUp,null,null).getAsJsonObject();
            result.add("DecayTime",new JsonPrimitive(powerUp.getDecayTime()));
            return result;
        }
    }

    private class PoptartPowerUpSerializer implements JsonSerializer<PoptartPowerUp>,JsonDeserializer<PoptartPowerUp>{
        @Override
        public JsonElement serialize(PoptartPowerUp poptartPowerUp, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new PowerUpSerializer().serialize(poptartPowerUp,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("Poptart"));
            return result;
        }

        @Override
        public PoptartPowerUp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new PoptartPowerUp(obj.get("X").getAsFloat(),obj.get("Y").getAsFloat(),obj.get("DecayTime").getAsFloat());
        }
    }

    private class LifePowerUpSerializer implements JsonSerializer<LifePowerUp>,JsonDeserializer<LifePowerUp>{
        @Override
        public JsonElement serialize(LifePowerUp lifePowerUp, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new PowerUpSerializer().serialize(lifePowerUp,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("Life"));
            return result;
        }

        @Override
        public LifePowerUp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new LifePowerUp(obj.get("X").getAsFloat(),obj.get("Y").getAsFloat(),obj.get("DecayTime").getAsFloat());
        }
    }

    private class PepsiPowerUpSerializer implements JsonSerializer<PepsiPowerUp>, JsonDeserializer<PepsiPowerUp>{
        @Override
        public JsonElement serialize(PepsiPowerUp PepsiPowerUp, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new PowerUpSerializer().serialize(PepsiPowerUp,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("Pepsi"));
            return result;
        }

        @Override
        public PepsiPowerUp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new PepsiPowerUp(obj.get("X").getAsFloat(),obj.get("Y").getAsFloat(),obj.get("DecayTime").getAsFloat());
        }
    }

    private class CrabPowerUpSerializer implements JsonSerializer<CrabPowerUp>, JsonDeserializer<CrabPowerUp>{
        @Override
        public JsonElement serialize(CrabPowerUp crabPowerUp, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new PowerUpSerializer().serialize(crabPowerUp,type,jsonSerializationContext).getAsJsonObject();
            result.add("Type",new JsonPrimitive("CrabPowerUp"));
            return result;
        }

        @Override
        public CrabPowerUp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new CrabPowerUp(obj.get("X").getAsFloat(),obj.get("Y").getAsFloat(),obj.get("DecayTime").getAsFloat());
        }
    }

    private class ParticleSerializer implements JsonSerializer<Particle>{
        @Override
        public JsonElement serialize(Particle particle, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result =new JsonObject();
            result.add("X",new JsonPrimitive(particle.getX()));
            result.add("Y",new JsonPrimitive(particle.getY()));
            result.add("Angle",new JsonPrimitive(particle.getAngle()));
            result.add("Speed",new JsonPrimitive(particle.getSpeed()));
            result.add("DecayTime", new JsonPrimitive(particle.getDecayTime()));
            return result;
        }
    }

    private class ExplosionParticleSerializer implements JsonSerializer<ExplosionParticle>,JsonDeserializer<ExplosionParticle>{
        @Override
        public JsonElement serialize(ExplosionParticle explosionParticle, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new ParticleSerializer().serialize(explosionParticle,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("Explosion"));
            return result;
        }

        @Override
        public ExplosionParticle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new ExplosionParticle(obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("DecayTime").getAsFloat());
        }
    }

    private class CollisionParticleSerializer implements JsonSerializer<CollisionParticle>,JsonDeserializer<CollisionParticle>{
        @Override
        public JsonElement serialize(CollisionParticle collisionParticle, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new ParticleSerializer().serialize(collisionParticle,null,null).getAsJsonObject();
            result.add("Type",new JsonPrimitive("Collision"));
            return result;
        }

        @Override
        public CollisionParticle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new CollisionParticle(   obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("Angle").getAsFloat(),
                    obj.get("Speed").getAsInt(),
                    obj.get("DecayTime").getAsFloat());
        }
    }

    private class PlayerSerializer implements JsonSerializer<Player>,JsonDeserializer<Player>{
        @Override
        public JsonElement serialize(Player player, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject result=new JsonObject();
            result.add("X",new JsonPrimitive(player.getX()));
            result.add("Y",new JsonPrimitive(player.getY()));
            result.add("HP",new JsonPrimitive(player.getHp()));
            result.add("MaxHP",new JsonPrimitive(player.getMaxHP()));
            result.add("Lives", new JsonPrimitive(player.getLives()));
            result.add("WeaponID",new JsonPrimitive(player.getWeaponIndex()));

            return result;
        }

        @Override
        public Player deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj=jsonElement.getAsJsonObject();
            return new Player(obj.get("X").getAsFloat(),
                    obj.get("Y").getAsFloat(),
                    obj.get("HP").getAsInt(),
                    obj.get("MaxHP").getAsInt(),
                    obj.get("Lives").getAsInt(),
                    obj.get("WeaponID").getAsInt());
        }
    }
}
