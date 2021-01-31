package de.sigabiel.bac.utils;

public enum EnumBuildType {

	BIG(0.375f), MEDIUM(0.562f), SMALL(0.812f);

	private float moveValue;

	private EnumBuildType(float moveValue) {
		this.moveValue = moveValue;
	}

	public float getMoveValue() {
		return moveValue;
	}

}
