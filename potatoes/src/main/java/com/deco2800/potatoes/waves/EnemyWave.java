package com.deco2800.potatoes.waves;

import com.deco2800.potatoes.entities.Tickable;
import com.deco2800.potatoes.entities.enemies.Moose;
import com.deco2800.potatoes.entities.enemies.SpeedyEnemy;
import com.deco2800.potatoes.entities.enemies.Squirrel;
import com.deco2800.potatoes.entities.enemies.TankEnemy;
import com.deco2800.potatoes.managers.GameManager;
import com.deco2800.potatoes.entities.GameTime;
import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;

public class EnemyWave implements Tickable {

    private float[] enemyRatios;    //Length of wave in .001 of seconds
    private int waveLength;     //The current time of the wave
    private int waveTime = 0;   //Spawn rate (100 = 1 second)
    private int spawnRate = 75;     //Time counting down for gui
    private int timeToEnd = 0;

    /*Probably can merge (waiting w/ finished) or (waiting w/ paused) not too sure*/
    public enum WaveState {
        WAITING, ACTIVE, PAUSED, FINISHED
    }

    WaveState waveState = WaveState.WAITING;

    /**
     * Create an EnemyWave. The rates for each enemy are relative, i.e if given 5,1,1,1 for each
     * enemyRate parameter, the relative rates of spawning for each type will be 5/8, 1/8, 1/8, 1/8.
     *
     * @param squirrelRate
     * @param speedyRate
     * @param tankRate
     * @param mooseRate
     * --can be changed to simply individual args for each enemy type - might actually be better
     * @param waveLength the length in minutes and seconds of wave (1.30) is 1 minute 30 seconds.
     * */
    public EnemyWave(int squirrelRate, int speedyRate, int tankRate, int mooseRate, int waveLength) {
        //addSquirrel();
        //addSquirrel();
        this.enemyRatios = calculateEnemyRatios(squirrelRate, speedyRate, tankRate, mooseRate);
        this.waveLength = waveLength;
        //spawnEnemyToRatio(enemyRatios);

    }

    /**
     * Create an array of floats between 0-1, where squirrelRate < speedyRate < tankRate < mooseRate.
     * The span of each value from itself to the immediate next highest ratio indicates its actual
     * ratio, i.e. if speedy is .50 and tank is .75, actual ratio is .25.
     *
     * @return an array of floats representing the ratio of enemy rates provided
     * */
    private float[] calculateEnemyRatios(float squirrelRate, float speedyRate, float tankRate, float mooseRate) {
        float total = squirrelRate + speedyRate + tankRate + mooseRate;
        // Ratios are the total spans of each; i.e. if speedyRatio is .50 and tank is .75, actual ratio is .25.
        float squirrelRatio = squirrelRate/total;
        float speedyRatio = squirrelRatio + speedyRate/total;
        float tankRatio = speedyRatio + tankRate/total;
        float mooseRatio = tankRatio + mooseRate/total;
        float[] enemyRatios = {squirrelRatio, speedyRatio, tankRatio, mooseRatio};
        return enemyRatios;
    }

    /**
     * Spawn enemies according to the ratio described by the constructor's different enemy rate arguments
     * */
    public void spawnEnemyToRatio(float[] enemyRatios) {
        Random random = new Random();
            float randomFloat = random.nextFloat();
            if (randomFloat < enemyRatios[0]) {
                addSquirrel();
            } else if (randomFloat < enemyRatios[1]) {
                addSpeedy();
            } else if (randomFloat < enemyRatios[2]) {
                addTank();
            } else if (randomFloat < enemyRatios[3]) {
                addMoose();
            }
    }

    /*May be an idea to make a safe guard spawn enemy... i.e. if an enemy has been specified to spawn a little bit
    but is unlucky enough not to have spawned... then spawn it.*/

    public void onTick(long i){
        //System.err.println(elapsedWaveTime());

        switch (getWaveState()) {
            case WAITING:
                //wait?
                break;
            case PAUSED:
                //wait?
                break;
            case ACTIVE:
                setCurrentWaveTime(elapsedWaveTime() + 1);
                if (elapsedWaveTime()>getWaveLength()) {
                    setWaveState(WaveState.FINISHED);
                } else if (elapsedWaveTime() % spawnRate == 0) {
                    spawnEnemyToRatio(enemyRatios);
                }
                //Check to see if wave is paused for some reason
                break;
            case FINISHED:
                //Handle finished state
                break;
        }
    }


    /**
     * @return the time elapsed since wave started in 0.001 seconds
     */
    public int elapsedWaveTime() { return waveTime; }

    /**
     * Sets the current wave Time.
     * @param CurrentTime
     */
    public void setCurrentWaveTime(int CurrentTime) { this.waveTime = CurrentTime; }

    public int getTimeToEnd() { return getWaveLength()-elapsedWaveTime(); }

    private void addSquirrel() {
        GameManager.get().getWorld().addEntity(new Squirrel(
                    24, 24, 0));
    }

    private void addTank() {
        GameManager.get().getWorld().addEntity(
                new TankEnemy(24, 24, 0));
    }

    private void addSpeedy() {
        GameManager.get().getWorld().addEntity(
                new SpeedyEnemy(24, 24, 0));

    }

    private void addMoose() {
        GameManager.get().getWorld().addEntity(new Moose(
                24, 24, 0));
    }

    public int getWaveLength() { return waveLength; }

    public void setWaveLength(int waveLength) { this.waveLength = waveLength; }

    public WaveState getWaveState() { return waveState; }

    public void setWaveState(WaveState state) { this.waveState = state; }

    public float[] getEnemyRatios(){ return this.enemyRatios;}

}

