/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.speech;

// [START speech_quickstart]
// Imports the Google Cloud client library
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import java.io.InputStream;
import java.util.List;

public class QuickstartSample {

	/**
	 * Demonstrates using the Speech API to transcribe an audio file.
	 */
	public static void main(String... args) throws Exception {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream credStream = classloader.getResourceAsStream("google_cred.json");

		GoogleCredentials credentials = GoogleCredentials.fromStream(credStream);
		FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

		SpeechSettings speechSettings =
				SpeechSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();

		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {

			// The path to the audio file to transcribe
			InputStream rawAudioStream = classloader.getResourceAsStream("Google_Gnome.wav");
			byte[] byteArray = ByteStreams.toByteArray(rawAudioStream);
			ByteString audioBytes = ByteString.copyFrom(byteArray);

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder()
					.setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(16000)
					.setLanguageCode("en-US")
					.build();
			RecognitionAudio audio = RecognitionAudio.newBuilder()
					.setContent(audioBytes)
					.build();

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech. Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
			}
		}
	}
}
// [END speech_quickstart]
