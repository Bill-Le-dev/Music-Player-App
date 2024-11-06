import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

// class used to describe a song
public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;
    private Artwork artwork;
    private byte[] imageData;

    public Song(String filePath){
        this.filePath = filePath;
        try{
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            songLength = convertToSongLengthFormat();

            // use the jaudiotagger library to create an audiofile obj to read mp3 file's information
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // read through the meta data of the audio file
            Tag tag =  audioFile.getTag();
            if(tag != null){
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
                artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    imageData = artwork.getBinaryData();

                    // Save the artwork to a file
                    File outputFile = new File("cover_art.jpg");
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        fos.write(imageData);  // <-- NullPointerException here
                        System.out.println("Artwork saved successfully to: " + outputFile.getAbsolutePath());
                    }
                } else {
                    System.out.println("No artwork found in the metadata.");
                }
            }else{
                // could not read through mp3 file's meta data
                songTitle = "N/A";
                songArtist = "N/A";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat(){
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    // getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public ImageIcon getImage() {
        if (artwork != null && imageData != null) {
            try {
                return new ImageIcon(imageData);  // Convert binary data to ImageIcon
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Load the default image from resources
        System.out.println("No artwork found, using default image.");
        return new ImageIcon(getClass().getResource("/images/default_cover_art.jpg"));  // Load from resources
    }

    public Mp3File getMp3File(){return mp3File;}
    public double getFrameRatePerMilliseconds(){return frameRatePerMilliseconds;}
}
















