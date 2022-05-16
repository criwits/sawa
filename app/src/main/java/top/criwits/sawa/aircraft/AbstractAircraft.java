package top.criwits.sawa.aircraft;

import top.criwits.sawa.bullet.AbstractBullet;
import top.criwits.sawa.basic.AbstractFlyingObject;
import top.criwits.sawa.prop.AbstractProp;

import java.util.List;

/**
 * Abstract class for all flying objects, including:
 *  - Enemies:
 *    - BOSS (BossEnemy)
 *    - ELITE (EliteEnemy)
 *    - MOB (MobEnemy)
 *  - The player controlled aircraft (HeroAircraft)
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    // Maximum HP (Health Points)
    protected int maxHp;
    // Health Points
    protected int hp;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public void increaseHp(int increase) {
        hp += increase;
        if (hp >= maxHp) {
            hp = maxHp;
        }
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }


    /**
     * 'Shooting' method for aircraft.
     * @return
     *  If an object can not shoot anything then return null;
     *  Otherwise, it will return a list of bullets.
     */
    public abstract List<AbstractBullet> shoot();

    /**
     * Can this aircraft generate loots?
     */
    public abstract List<AbstractProp> generateProp();

    public abstract int addScore();

}