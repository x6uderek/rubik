package com.derek.android.rubik.robot;

import android.util.Log;

import com.derek.android.rubik.Action;
import com.derek.android.rubik.Color;
import com.derek.android.rubik.Cube;
import com.derek.android.rubik.Cube3;
import com.derek.android.rubik.Cube3.Layer;
import com.derek.android.rubik.LayerRotate;
import com.derek.android.rubik.RubikRotate;

public class DefaultAI implements IRobot {
    Action[] actionBuffer;
    int currentIndex = -1;
    private final Step[] steps = new Step[7];
    byte currentStep = 0;
    Action.ActionCallback callback;

    public DefaultAI(Action.ActionCallback callback) {
        this.callback = callback;
        steps[0] = new Step1();
        steps[1] = new Step2();
        steps[2] = new Step3();
        steps[3] = new Step4();
        steps[4] = new Step5();
        steps[5] = new Step6();
        steps[6] = new Step7();
    }

    @Override
    public void enableAI() {
        chooseStep();
    }

    @Override
    public Action next() {
        if (actionBuffer == null) {  // execute only once for the first time
            String rule = getRule();
            if (rule == null) {
                return null;
            }
            actionBuffer = getActionFromStrategy(rule);
        }
        currentIndex++;
        if (currentIndex >= actionBuffer.length) {  //run out of buffer
            String rule = getRule();
            if (rule == null) {
                return null;
            }
            actionBuffer = getActionFromStrategy(rule);
            currentIndex = 0;
        }
        return actionBuffer[currentIndex];
    }

    private String getRule() {
        String rule = steps[currentStep].getStrategy();
        while (rule == null && currentStep < steps.length - 1) {
            currentStep++;
            rule = steps[currentStep].getStrategy();
        }
        return rule;
    }

    @Override
    public void disableAI() {
    }

    private void chooseStep() {
        switch (checkCompleteLayer()) {
        case 0:
            currentStep = 0;
            break;
        case 1:
            currentStep = 2;
            break;
        case 2:
            currentStep = 3;
            break;
        case 3:
            currentStep = 6;
            break;
        }
    }

    public Action[] getActionFromStrategy(String rule) {
        int count = rule.length();
        Action[] result = new Action[count];
        for (int i = 0; i < count; i++) {
            switch (rule.charAt(i)) {
            case 'u':
                result[i] = new LayerRotate(Cube3.topY, true, callback);
                break;
            case 'U':
                result[i] = new LayerRotate(Cube3.topY, false, callback);
                break;

            case 'd':
                result[i] = new LayerRotate(Cube3.bottomY, true, callback);
                break;
            case 'D':
                result[i] = new LayerRotate(Cube3.bottomY, false, callback);
                break;

            case 'l':
                result[i] = new LayerRotate(Cube3.leftX, true, callback);
                break;
            case 'L':
                result[i] = new LayerRotate(Cube3.leftX, false, callback);
                break;

            case 'r':
                result[i] = new LayerRotate(Cube3.rightX, false, callback);
                break;
            case 'R':
                result[i] = new LayerRotate(Cube3.rightX, true, callback);
                break;

            case 'f':
                result[i] = new LayerRotate(Cube3.frontZ, false, callback);
                break;
            case 'F':
                result[i] = new LayerRotate(Cube3.frontZ, true, callback);
                break;

            case 'b':
                result[i] = new LayerRotate(Cube3.backZ, true, callback);
                break;
            case 'B':
                result[i] = new LayerRotate(Cube3.backZ, false, callback);
                break;

            case 'm':
                result[i] = new LayerRotate(Cube3.middleY, true, callback);
                break;
            case 'M':
                result[i] = new LayerRotate(Cube3.middleY, false, callback);
                break;

            case 't':
                result[i] = new RubikRotate(Layer.axisY, true, callback);
                break;
            case 'T':
                result[i] = new RubikRotate(Layer.axisY, false, callback);
                break;
            }
        }
        return result;
    }

    public static int checkCompleteLayer() {
        int i, j;
        Color temp = Cube3.getInstance().getCubeColor(1, 0, 1, Cube.bottom);
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (!temp.equals(Cube3.getInstance().getCubeColor(i, 0, j, Cube.bottom))) {
                    return 0;
                }
            }
        }
        for (j = 0; j < 3; j++) {
            temp = Cube3.getInstance().getCubeColor(0, j, 0, Cube.left);
            for (i = 1; i < 3; i++) {
                if (!temp.equals(Cube3.getInstance().getCubeColor(0, j, i, Cube.left))) {
                    return j;
                }
            }
            temp = Cube3.getInstance().getCubeColor(0, j, 2, Cube.front);
            for (i = 1; i < 3; i++) {
                if (!temp.equals(Cube3.getInstance().getCubeColor(i, j, 2, Cube.front))) {
                    return j;
                }
            }
            temp = Cube3.getInstance().getCubeColor(2, j, 0, Cube.right);
            for (i = 1; i < 3; i++) {
                if (!temp.equals(Cube3.getInstance().getCubeColor(2, j, i, Cube.right))) {
                    return j;
                }
            }
            temp = Cube3.getInstance().getCubeColor(0, j, 0, Cube.back);
            for (i = 1; i < 3; i++) {
                if (!temp.equals(Cube3.getInstance().getCubeColor(i, j, 0, Cube.back))) {
                    return j;
                }
            }
        }
        return 3;
    }

    interface Step {
        public byte getState();
        public String getStrategy();
    }

    class Step1 implements Step {
        public static final String rule1_1 = "RR";
        public static final String rule1_2 = "ufRF";
        private byte correctCount = 0;
        private static final byte state_correct = 0;
        private static final byte state_top_right_1 = 1;
        private static final byte state_top_front = 2;
        private static final byte state_top_back = 3;
        private static final byte state_top_left = 4;
        private static final byte state_middle_right = 5;
        private static final byte state_middle_left = 6;
        private static final byte state_middle_front = 7;
        private static final byte state_middle_back = 8;
        private static final byte state_bottom_front = 9;
        private static final byte state_bottom_back = 10;
        private static final byte state_bottom_left = 11;
        private static final byte state_bottom_right = 12;
        private static final byte state_top_right_2 = 13;

        @Override
        public byte getState() {
            Color colorBottom = Cube3.getInstance().getCubeColor(1, 0, 1, Cube.bottom);
            Color colorRight = Cube3.getInstance().getCubeColor(2, 1, 1, Cube.right);
            Color color1 = Cube3.getInstance().getCubeColor(2, 0, 1, Cube.right);
            Color color2 = Cube3.getInstance().getCubeColor(2, 0, 1, Cube.bottom);
            if (color1.equals(colorRight) && color2.equals(colorBottom)) {
                return state_correct;
            }
            if (color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_bottom_right;
            }
            color1 = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.right);
            color2 = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.top);
            if (color1.equals(colorRight) && color2.equals(colorBottom)) {
                return state_top_right_1;
            }
            if (color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_top_right_2;
            }
            color1 = Cube3.getInstance().getCubeColor(0, 2, 1, Cube.left);
            color1 = Cube3.getInstance().getCubeColor(0, 2, 1, Cube.top);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_top_left;
            }
            color1 = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.front);
            color2 = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.top);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_top_front;
            }
            color1 = Cube3.getInstance().getCubeColor(1, 2, 0, Cube.back);
            color2 = Cube3.getInstance().getCubeColor(1, 2, 0, Cube.top);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_top_back;
            }
            color1 = Cube3.getInstance().getCubeColor(2, 1, 2, Cube.front);
            color2 = Cube3.getInstance().getCubeColor(2, 1, 2, Cube.right);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_middle_front;
            }
            color1 = Cube3.getInstance().getCubeColor(2, 1, 0, Cube.right);
            color2 = Cube3.getInstance().getCubeColor(2, 1, 0, Cube.back);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_middle_right;
            }
            color1 = Cube3.getInstance().getCubeColor(0, 1, 2, Cube.left);
            color2 = Cube3.getInstance().getCubeColor(0, 1, 2, Cube.front);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_middle_left;
            }
            color1 = Cube3.getInstance().getCubeColor(0, 1, 0, Cube.left);
            color2 = Cube3.getInstance().getCubeColor(0, 1, 0, Cube.back);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_middle_back;
            }
            color1 = Cube3.getInstance().getCubeColor(1, 0, 2, Cube.front);
            color2 = Cube3.getInstance().getCubeColor(1, 0, 2, Cube.bottom);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_bottom_front;
            }
            color1 = Cube3.getInstance().getCubeColor(0, 0, 1, Cube.left);
            color2 = Cube3.getInstance().getCubeColor(0, 0, 1, Cube.bottom);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_bottom_left;
            }
            color1 = Cube3.getInstance().getCubeColor(1, 0, 0, Cube.back);
            color2 = Cube3.getInstance().getCubeColor(1, 0, 0, Cube.bottom);
            if (color1.equals(colorRight) && color2.equals(colorBottom)
                    || color1.equals(colorBottom) && color2.equals(colorRight)) {
                return state_bottom_back;
            }
            return 0;
        }

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            switch(state) {
            case state_correct:
                if(correctCount >= 4) {
                    result = null;
                    correctCount = 0;
                }
                else {
                    result = "t";
                    correctCount++;
                }
                break;
            case state_top_right_1:
                result = rule1_1;
                correctCount = 0;
                break;
            case state_top_right_2:
                result = rule1_2;
                correctCount = 0;
                break;
            case state_top_front:
                result = "U";
                correctCount = 0;
                break;
            case state_top_back:
                result = "u";
                correctCount = 0;
                break;
            case state_top_left:
                result = "UU";
                correctCount = 0;
                break;
            case state_middle_right:
                result = "R";
                correctCount = 0;
                break;
            case state_middle_left:
                result = "LUUl";
                correctCount = 0;
                break;
            case state_middle_front:
                result = "r";
                correctCount = 0;
                break;
            case state_middle_back:
                result = "lUUL";
                correctCount = 0;
                break;
            case state_bottom_front:
                result = "FFUff";
                correctCount = 0;
                break;
            case state_bottom_back:
                result = "bbuBB";
                correctCount = 0;
                break;
            case state_bottom_left:
                result = "LLUUll";
                correctCount = 0;
                break;
            case state_bottom_right:
                result = "rr";
                correctCount = 0;
                break;
            }
            return result;
        }
    }

    class Step2 implements Step {
        public static final String rule2_1 = "ruR";
        public static final String rule2_2 = "FUf";
        private static final byte correct = 0;
        private static final byte state1 = 1;
        private static final byte state2 = 2;
        private static final byte state3 = 3;
        private static final byte state4 = 4;
        private static final byte state5 = 5;
        private static final byte middle_wrong = 6;
        private static final byte top_wrong = 7;
        private static final byte may_lock = 8;
        private byte wrongCount = 0;

        @Override
        public byte getState() {
            Color left = Cube3.getInstance().getCubeColor(1, 0, 2, Cube.front);
            Color right = Cube3.getInstance().getCubeColor(2, 0, 1, Cube.right);
            Color bottom = Cube3.getInstance().getCubeColor(1, 0, 1, Cube.bottom);
            Color leftCenter = Cube3.getInstance().getCubeColor(1, 1, 2, Cube.front);
            Color rightCenter = Cube3.getInstance().getCubeColor(2, 1, 1, Cube.right);
            Color topTop = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.top);
            Color topLeft = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front);
            Color topRight = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.right);
            Color bottomBottom = Cube3.getInstance().getCubeColor(2, 0, 2, Cube.bottom);
            Color bottomLeft = Cube3.getInstance().getCubeColor(2, 0, 2, Cube.front);
            Color bottomRight = Cube3.getInstance().getCubeColor(2, 0, 2, Cube.right);
            if (!leftCenter.equals(left) || !rightCenter.equals(right)) {
                return middle_wrong;
            }
            if (bottomBottom.equals(bottom) && bottomLeft.equals(left) && bottomRight.equals(right)) {
                return correct;
            }
            if (topTop.equals(right) && topLeft.equals(left) && topRight.equals(bottom)) {
                return state1;
            }
            if (topTop.equals(left) && topLeft.equals(bottom) && topRight.equals(right)) {
                return state2;
            }
            if (bottomBottom.equals(left) && bottomLeft.equals(right) && bottomRight.equals(bottom)) {
                return state3;
            }
            if (bottomBottom.equals(right) && bottomLeft.equals(bottom) && bottomRight.equals(left)) {
                return state4;
            }
            if (topTop.equals(bottom) && topLeft.equals(right) && topRight.equals(left)) {
                return state5;
            }
            if (bottomBottom.equals(bottom) || bottomLeft.equals(bottom) || bottomRight.equals(bottom)) {
                return may_lock;
            }
            return top_wrong;
        }

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            Log.i("Step2", String.valueOf(state));
            switch (state) {
            case correct:
                if (DefaultAI.checkCompleteLayer() != 0) {
                    result = null;
                }
                else {
                    result = "t";
                }
                wrongCount = 0;
                break;
            case state1:
                result = rule2_1;
                wrongCount = 0;
                break;
            case state2:
                result = rule2_2;
                wrongCount = 0;
                break;
            case state3:
                result = rule2_1 + "U" + rule2_1;
                wrongCount = 0;
                break;
            case state4:
                result = rule2_2 + "u" + rule2_2;
                wrongCount = 0;
                break;
            case state5:
                result = rule2_1 + rule2_1 + "U" + rule2_1;
                wrongCount = 0;
                break;
            case middle_wrong:
                result = "m";
                wrongCount = 0;
                break;
            case may_lock:
                result = rule2_1;
                wrongCount = 0;
                break;
            case top_wrong:
                if (wrongCount >= 4) {
                    result = "t";
                    wrongCount = 0;
                }
                else {
                    result = "u";
                    wrongCount++;
                }
                break;
            }
            return result;
        }
    }

    class Step3 implements Step {
        private static final byte state_left = 0;
        private static final byte state_right = 1;
        private static final byte state_opposite = 2;
        private static final byte state_middle_wrong = 3;
        private static final byte state_top_wrong = 4;
        private static final byte state_correct = 5;
        private static final byte state_may_lock = 6;
        private static final String rule_3_1 = "UFufurUR";
        private static final String rule_3_2 = "urURUFuf";

        private byte wrongCount = 0;

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            Log.i("Step3", String.valueOf(state));
            switch (state) {
            case state_middle_wrong:
                result = "m";
                wrongCount = 0;
                break;
            case state_left:
                result = rule_3_2;
                wrongCount = 0;
                break;
            case state_right:
                result = rule_3_1;
                wrongCount = 0;
                break;
            case state_opposite:
                result = rule_3_2; //?????????????????????
                wrongCount = 0;
                break;
            case state_may_lock:
                result = rule_3_2;
                wrongCount = 0;
                break;
            case state_correct:
                if (DefaultAI.checkCompleteLayer() != 1) {
                    result = null;
                }
                else {
                    result = "t";
                }
                wrongCount = 0;
                break;
            case state_top_wrong:
                if (wrongCount >= 4) {
                    result = "t";
                    wrongCount = 0;
                }
                else {
                    result = "u";
                    wrongCount++;
                }
                break;
            }
            return result;
        }

        @Override
        public byte getState() {
            Color leftColor = Cube3.getInstance().getCubeColor(0, 0, 2, Cube.front);
            Color leftCenterColor = Cube3.getInstance().getCubeColor(1, 1, 2, Cube.front);
            Color rightColor = Cube3.getInstance().getCubeColor(2, 0, 0, Cube.right);
            Color rightCenterColor = Cube3.getInstance().getCubeColor(2, 1, 1, Cube.right);
            Color leftTargetColor = Cube3.getInstance().getCubeColor(2, 1, 2, Cube.front);
            Color rightTargetColor = Cube3.getInstance().getCubeColor(2, 1, 2, Cube.right);
            Color leftUpColor = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.front);
            Color rightUpColor = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.right);
            Color leftTopColor = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.top);
            Color rightTopColor = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.top);
            Color topColor = Cube3.getInstance().getCubeColor(1, 2, 1, Cube.top);
            if (!leftCenterColor.equals(leftColor) || !rightCenterColor.equals(rightColor)) {
                return state_middle_wrong;
            }
            if (leftTargetColor.equals(rightColor) && rightTargetColor.equals(leftColor)) {
                return state_opposite;
            }
            if (leftUpColor.equals(leftColor) && leftTopColor.equals(rightColor)) {
                return state_left;
            }
            if (rightUpColor.equals(rightColor) && rightTopColor.equals(leftColor)) {
                return state_right;
            }
            if (leftTargetColor.equals(leftColor) && rightTargetColor.equals(rightColor)) {
                return state_correct;
            }
            if (!leftTargetColor.equals(topColor) && !rightTargetColor.equals(topColor)) {
                return state_may_lock;
            }
            return state_top_wrong;
        }
    }

    class Step4 implements Step {
        private static final byte state_cross = 0;
        private static final byte state_one_face_line = 1;
        private static final byte state_two_face_dot = 2;
        private static final byte state_two_face_v = 3;
        private static final byte state_wrong = 4;
        private static final String rule4 = "fruRUF";
        @Override
        public String getStrategy() {
            String result = "u";
            byte state = getState();
            Log.i("Step4", String.valueOf(state));
            switch (state) {
            case state_cross:
                result = null;
                break;
            case state_one_face_line:
                result = rule4;
                break;
            case state_two_face_dot:
                result = rule4 + rule4 + "u" + rule4;
                break;
            case state_two_face_v:
                result = rule4 + rule4;
                break;
            case state_wrong:
                break;
            }
            return result;
        }
        @Override
        public byte getState() {
            Color centerColor = Cube3.getInstance().getCubeColor(1, 2, 1, Cube.top);
            Color leftColor = Cube3.getInstance().getCubeColor(0, 2, 1, Cube.top);
            Color rightColor = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.top);
            Color downColor = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.top);
            Color upColor = Cube3.getInstance().getCubeColor(1, 2, 0, Cube.top);

            Color frontColor = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.front);
            Color rightRightColor = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.right);

            if (leftColor.equals(centerColor) && rightColor.equals(centerColor)
                    && downColor.equals(centerColor) && upColor.equals(centerColor)) {
                return state_cross;
            }
            if (leftColor.equals(centerColor) && rightColor.equals(centerColor) && frontColor.equals(centerColor)) {
                return state_one_face_line;
            }
            if (leftColor.equals(centerColor) && upColor.equals(centerColor)
                    && frontColor.equals(centerColor) && rightRightColor.equals(centerColor)) {
                return state_two_face_v;
            }
            if (frontColor.equals(centerColor) && rightRightColor.equals(centerColor)) {
                return state_two_face_dot;
            }
            return state_wrong;
        }
    }

    class Step5 implements Step {
        private static final byte complete = 0;
        private static final byte state1 = 1;
        private static final byte state2 = 2;
        private static final byte state3 = 3;
        private static final byte state4 = 4;
        private static final byte state5 = 5;
        private static final byte state6 = 6;
        private static final byte state7 = 7;
        private static final byte wrong = 8;

        public static final String rule5_1 = "RuuruRur";
        public static final String rule5_2 = "UrUURUrUR";

        @Override
        public byte getState() {
            int i = 0, j = 0;
            boolean flag = false;
            Color center = Cube3.getInstance().getCubeColor(1, 2, 1, Cube.top);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (!Cube3.getInstance().getCubeColor(i, 2, j, Cube.top).equals(center)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
            if (i == 3 && j == 3) {
                return complete;
            }
            Color rightBottom = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.top);
            if (center.equals(rightBottom)) {
                Color color1 = Cube3.getInstance().getCubeColor(2, 2, 0, Cube.right);
                Color color2 = Cube3.getInstance().getCubeColor(0, 2, 0, Cube.back);
                Color color3 = Cube3.getInstance().getCubeColor(0, 2, 2, Cube.left);
                if (color1.equals(center) && color2.equals(center) && color3.equals(center)) {
                    return state1;
                }
                Color color4 = Cube3.getInstance().getCubeColor(2, 2, 0, Cube.back);
                Color color5 = Cube3.getInstance().getCubeColor(0, 2, 0, Cube.left);
                Color color6 = Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front);
                if (color4.equals(center) && color5.equals(center) && color6.equals(center)) {
                    return state2;
                }
            }
            Color rightTop = Cube3.getInstance().getCubeColor(2, 2, 0, Cube.top);
            Color leftBottom = Cube3.getInstance().getCubeColor(0, 2, 2, Cube.top);
            if (rightTop.equals(center) && leftBottom.equals(center)) {
                Color color1 = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.right);
                Color color2 = Cube3.getInstance().getCubeColor(0, 2, 0, Cube.back);
                if (color1.equals(center) && color2.equals(center)) {
                    return state3;
                }
            }
            Color leftTop = Cube3.getInstance().getCubeColor(0, 2, 0, Cube.top);
            if (leftTop.equals(center) && leftBottom.equals(center)) {
                Color color1 = Cube3.getInstance().getCubeColor(2, 2, 0, Cube.back);
                Color color2 = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front);
                if (color1.equals(center) && color2.equals(center)) {
                    return state4;
                }
            }
            if (leftTop.equals(center) && rightTop.equals(center)) {
                Color color1 = Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front);
                Color color2 = Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front);
                if (color1.equals(center) && color2.equals(center)) {
                    return state5;
                }
            }
            if (Cube3.getInstance().getCubeColor(2, 2, 0, Cube.right).equals(center) &&
                    Cube3.getInstance().getCubeColor(0, 2, 0, Cube.left).equals(center) &&
                    Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front).equals(center) &&
                    Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front).equals(center)) {
                return state6;
            }
            if (Cube3.getInstance().getCubeColor(2, 2, 0, Cube.back).equals(center) &&
                    Cube3.getInstance().getCubeColor(0, 2, 0, Cube.back).equals(center) &&
                    Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front).equals(center) &&
                    Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front).equals(center)) {
                return state7;
            }
            return wrong;
        }

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            Log.i("Step5", String.valueOf(state));
            switch (state) {
            case complete:
                break;
            case state1:
                result = rule5_1;
                break;
            case state2:
                result = rule5_2;
                break;
            case state3:
                result = rule5_1 + rule5_2;
                break;
            case state4:
                result = rule5_2 + rule5_1;
                break;
            case state5:
                result = rule5_2 + "uu" + rule5_1;
                break;
            case state6:
                result = rule5_1 + "U" + rule5_1;
                break;
            case state7:
                result = rule5_1 + rule5_1;
                break;
            case wrong:
                result = "u";
                break;
            }
            return result;
        }
    }

    class Step6 implements Step {
        private static final byte complete = 0;
        private static final byte correct = 1;
        private static final byte wrong = 2;
        private byte wrongCount = 0;

        public static final String rule6 = "rBrffRbrffrr";

        @Override
        public byte getState() {
            Color front = Cube3.getInstance().getCubeColor(1, 1, 2, Cube.front);
            Color left = Cube3.getInstance().getCubeColor(0, 1, 1, Cube.left);
            Color right = Cube3.getInstance().getCubeColor(2, 1, 1, Cube.right);
            Color back = Cube3.getInstance().getCubeColor(1, 1, 0, Cube.back);
            if (Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front).equals(front) &&
                    Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front).equals(front) &&
                    Cube3.getInstance().getCubeColor(2, 2, 2, Cube.right).equals(right) &&
                    Cube3.getInstance().getCubeColor(2, 2, 0, Cube.right).equals(right) &&
                    Cube3.getInstance().getCubeColor(2, 2, 0, Cube.back).equals(back) &&
                    Cube3.getInstance().getCubeColor(0, 2, 0, Cube.back).equals(back) &&
                    Cube3.getInstance().getCubeColor(0, 2, 0, Cube.left).equals(left) &&
                    Cube3.getInstance().getCubeColor(0, 2, 2, Cube.left).equals(left)) {
                return complete;
            }
            if (Cube3.getInstance().getCubeColor(0, 2, 2, Cube.front).equals(Cube3.getInstance().getCubeColor(2, 2, 2, Cube.front))) {
                return correct;
            }
            return wrong;
        }

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            Log.i("Step6", String.valueOf(state));
            switch (state) {
            case wrong:
                if (wrongCount >= 4) {
                    result = rule6;
                    wrongCount = 0;
                }
                else {
                    result = "u";
                    wrongCount++;
                }
                break;
            case correct:
                result = rule6;
                wrongCount = 0;
                break;
            case complete:
                wrongCount = 0;
                break;
            }
            return result;
        }
    }

    class Step7 implements Step {
        //public static final String rule7 = "rUrururURUrr";
        public static final String rule7_1 = "RUrURUUr";
        public static final String rule7_2 = "fuFufuuF";
        private static final byte complete = 0;
        private static final byte state1 = 1;
        private static final byte state2 = 2;
        private static final byte state3 = 3;
        private static final byte state4 = 4;
        private static final byte wrong = 5;
        @Override
        public byte getState() {
            Color back = Cube3.getInstance().getCubeColor(1, 1, 0, Cube.back);
            Color front = Cube3.getInstance().getCubeColor(1, 1, 2, Cube.front);
            Color left = Cube3.getInstance().getCubeColor(0, 1, 1, Cube.left);
            Color right = Cube3.getInstance().getCubeColor(2, 1, 1, Cube.right);

            Color leftTop = Cube3.getInstance().getCubeColor(0, 2, 1, Cube.left);
            Color frontTop = Cube3.getInstance().getCubeColor(1, 2, 2, Cube.front);
            Color rightTop = Cube3.getInstance().getCubeColor(2, 2, 1, Cube.right);
            Color backTop = Cube3.getInstance().getCubeColor(1, 2, 0, Cube.back);

            if (backTop.equals(back)) {
                if (frontTop.equals(front) && leftTop.equals(left) && rightTop.equals(right)) {
                    return complete;
                }
                if (leftTop.equals(front) && frontTop.equals(right) && rightTop.equals(left)) {
                    return state1;
                }
                if (rightTop.equals(front) && frontTop.equals(left) && leftTop.equals(right)) {
                    return state2;
                }
            }
            if (frontTop.equals(back) && backTop.equals(front) && leftTop.equals(right) && rightTop.equals(left)) {
                return state3;
            }
            if (frontTop.equals(right) && backTop.equals(left) && leftTop.equals(back) && rightTop.equals(front)) {
                return state4;
            }
            return wrong;
        }

        @Override
        public String getStrategy() {
            String result = null;
            byte state = getState();
            Log.i("Step7", String.valueOf(state));
            switch (state) {
            case wrong:
                result = "t";
                break;
            case state1:
                result = "tt" + rule7_1 + "tt" + rule7_2;
                break;
            case state2:
                result = "T" + rule7_2 + "tt" + rule7_1;
                break;
            case state3:
            case state4:
                result = rule7_1 + "tt" + rule7_2;
                break;
            case complete:
                break;
            }
            return result;
        }
    }
}
