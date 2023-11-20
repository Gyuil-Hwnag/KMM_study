//
//  VoicRecorderButton.swift
//  iosApp
//
//  Created by Duru on 2023/11/20.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct VoiceRecorderButton: View {
    var displayState: DisplayState
    var onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            ZStack {
                Circle()
                    .foregroundColor(.primaryColor)
                    .padding()
                icon
                    .foregroundColor(.onPrimary)
            }
        }
        .frame(maxWidth: 100.0, maxHeight: 100.0)
    }

    var icon: some View {
        switch displayState {
        case .speaking:
            return Image(systemName: "stop.fill")
        case .displayingResults:
            return Image(systemName: "checkmark")
        default:
            return Image(uiImage: UIImage(named: "mic")!)
        }
    }
}

struct VoiceRecorderButton_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            VoiceRecorderButton(
                displayState: .speaking,
                onClick: {}
            )
            VoiceRecorderButton(
                displayState: .waitingToTalk,
                onClick: {}
            )
            VoiceRecorderButton(
                displayState: .displayingResults,
                onClick: {}
            )
        }
        
    }
}
