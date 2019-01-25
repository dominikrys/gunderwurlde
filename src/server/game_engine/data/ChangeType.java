package server.game_engine.data;

public enum ChangeType {
    NONE, // No change was made
    BASIC_CHANGE, // simple change was made e.g. location or pose
    FULL_CHANGE, // a change was made to values that differs to it's defaults e.g. speed or type
    NEW, // it's a new object with default values or the object changed type.
    REMOVED; // the object was removed
}
