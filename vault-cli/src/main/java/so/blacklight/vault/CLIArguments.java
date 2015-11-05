package so.blacklight.vault;

import com.github.jankroken.commandline.annotations.*;

public class CLIArguments {

    private boolean listRequested = false;

    private String filename;

    @Option
    @LongSwitch("list")
    @ShortSwitch("l")
    @Toggle(true)
    public void setListRequested(boolean request) {
        listRequested = request;
    }

    public boolean isListRequested() {
        return listRequested;
    }

    @Option
    @LongSwitch("file")
    @ShortSwitch("f")
    @SingleArgument
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
