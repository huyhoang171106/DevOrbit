package vn.edu.uit.devorbit_api.entity;

/**
 * What a Note is attached to.
 *
 * COURSE — The note is about a specific course
 * REPO   — The note is about a specific GitHub repo
 * NONE   — General note not attached to anything specific
 */
public enum NoteTargetType {
    COURSE,
    REPO,
    NONE
}
