package com.quest1.demopos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quest1.demopos.R
import com.quest1.demopos.data.model.core.TerminalInfo
import com.quest1.demopos.ui.theme.LightTextPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalId(terminalId: String, terminalInfo: TerminalInfo) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(50),
        color = LightTextPrimary,
        modifier = Modifier.padding(start = 16.dp),

    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = terminalId,
                color = Color.White,
                fontSize = 14.sp
            )
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(spacingBetweenTooltipAndAnchor = 10.dp),
                modifier = Modifier.padding(start = 4.dp),
                tooltip = {
                    PlainTooltip {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Local Peer Id: ${terminalInfo.peerKey ?: "N/A"}",
                                color = Color.White,
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Connection Status: ",
                                    color = Color.White,
                                    )
                                Spacer(modifier = Modifier.width(4.dp))
                                ConnectionStatusIndicator(isConnected = terminalInfo.isConnected)
                            }
                        }
                    }
                },
                state = tooltipState
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info_24px),
                    contentDescription = "Terminal Info",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { scope.launch { tooltipState.show() } }
                )
            }
        }
    }
}

