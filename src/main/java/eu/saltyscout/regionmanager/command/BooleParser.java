package eu.saltyscout.regionmanager.command;


import eu.saltyscout.booleregion.exception.InvalidBooleanLogicException;
import eu.saltyscout.booleregion.region.*;
import eu.saltyscout.regionmanager.lang.Lang;
import eu.saltyscout.regionmanager.region.Region;
import eu.saltyscout.regionmanager.region.RegionRegistry;
import eu.saltyscout.regionmanager.region.RegionType;
import eu.saltyscout.regionmanager.region.impl.ShapedRegion;

/**
 * Created by Peter on 19-Nov-16.
 */
public class BooleParser {
    private BooleParser() {

    }

    /**
     * Parses boolean logic into a BooleRegion.
     * @param str the boolean logic to work with.
     * @return a {@link BooleRegion} construct.
     * @throws UnsupportedOperationException: These are meant to transfer errors out of the parse algorithm back to the user.
     * DO NOT CATCH SILENTLY.
     */
    public synchronized static BooleRegion parse(String str) throws UnsupportedOperationException {
        BooleRegion region;
        str = str.replace(" ", "");
        //System.out.println("Parsing '" + str + "'");
        char[] chars = str.toCharArray();
        int operatorPosition = findOperator(chars);
        if (operatorPosition < 0) {
            Region targetRegion = RegionRegistry.getRegion(str);
            if (targetRegion == null) {
                throw new InvalidBooleanLogicException(Lang.REGION_NOT_FOUND);
            } else if (targetRegion.getType() == RegionType.GLOBAL) {
                throw new InvalidBooleanLogicException(Lang.CANNOT_SELECT_GLOBAL);
            } else {
                ShapedRegion shaped = (ShapedRegion) targetRegion;
                region = new RegionWrapper(shaped.getShape());
            }
        } else if (operatorPosition < 1) {
            throw new InvalidBooleanLogicException(Lang.INCOMPLETE_BOOLE_FORMAT);
        } else {
            char[] partA = new char[operatorPosition];
            System.arraycopy(chars, 0, partA, 0, partA.length);
            char[] partB = new char[chars.length - operatorPosition - 1];
            System.arraycopy(chars, operatorPosition + 1, partB, 0, partB.length);
            String a = new String(partA);
            String b = new String(partB);
            if (partA[0] == '(' && partA[partA.length - 1] == ')') {
                a = a.substring(1, a.length() - 1);
            }
            if (partB[0] == '(' && partB[partB.length - 1] == ')') {
                b = b.substring(1, b.length() - 1);
            }
            char operator = chars[operatorPosition];
            region = applyOperation(parse(a), operator, parse(b));
        }
        return region;
    }

    private static BooleRegion applyOperation(BooleRegion regionA, char operation, BooleRegion regionB) {
        BooleRegion region = null;
        switch (operation) {
            case '*': {
                region = new IntersectionWrapper(regionA, regionB);
                break;
            }
            case '+': {
                region = new UnionWrapper(regionA, regionB);
                break;
            }
            case '\\': {
                region = new DifferenceWrapper(regionA, regionB);
                break;
            }
        }
        return region;
    }

    private static int findOperator(char[] chars) {
        int level = 0;
        if (chars[0] == '(') {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '(') {
                    level++;
                } else if (chars[i] == ')') {
                    level--;
                }
                if (level == 0) {
                    switch (chars[i]) {
                        case '*':
                            return i;
                        case '\\':
                            return i;
                        case '+':
                            return i;
                    }
                }
            }
        } else {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '*' || chars[i] == '\\' || chars[i] == '+') {
                    return i;
                }
            }
        }
        return -1;
    }

}
