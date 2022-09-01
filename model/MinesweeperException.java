package model;


public class MinesweeperException extends Exception {
        private String message;
        /**
         * Creates a new MineSweeperException with the specified message.
         * 
         * @param message The message describing the error that caused the 
         * exception.
         */
        public MinesweeperException(String message) {
            super(message);
            this.message = message;
        }
        public String getMessage() {
            return this.message;
        }
}
