package com.javacup.tactics.romedal.util;

import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;

public interface RMConstants {
static final double TO_ANG = 180 / Math.PI;
static final double TO_RAD = Math.PI / 180;
static final double META_Y = Constants.FIELD_LENGTH / 2;
static final double MAX_ANGLE = 45;
static final double MIN_ANGLE = 13;
static final double DELTA_ANGLE = 0.5;
static final double SENO_TETA = 2.3;
static final double G = 9.8;

static final double STATIC_HEIGHT_CALC = Math.pow(SENO_TETA, 2) * Constants.TRAJECTORY_VELOCITY_AMPLIFIER / G;

static final double MAX_SHOT_SPEED = Constants.getKickVelocity(1);

static final int MAX_ITER = 50;
static final double SPEED_APROX = 0.36d;
static final double FLAT_SHOT = 12;

static Position metaAbajoIzquierda = new Position(-Constants.SMALL_AREA_LENGTH/2, Constants.FIELD_LENGTH/2 - Constants.SMALL_AREA_WIDTH);
static Position metaAbajoDerecha = new Position(Constants.SMALL_AREA_LENGTH/2, Constants.FIELD_LENGTH/2 - Constants.SMALL_AREA_WIDTH);
static Position metaArribaIzquierda = new Position(-Constants.SMALL_AREA_LENGTH/2, -Constants.FIELD_LENGTH/2);
static Position metaArribaDerecha = new Position(Constants.SMALL_AREA_LENGTH/2, -Constants.FIELD_LENGTH/2);

public final int idxMyGoalkeeper = 0;

public static double MEDIO = Constants.FIELD_LENGTH / 4;

}
