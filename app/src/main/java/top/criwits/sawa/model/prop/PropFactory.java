package top.criwits.sawa.model.prop;

import top.criwits.sawa.model.basic.AbstractFactory;

public interface PropFactory extends AbstractFactory {
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY);
}
