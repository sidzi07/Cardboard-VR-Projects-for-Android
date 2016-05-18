precision mediump float;        // default medium precision
uniform sampler2D u_Texture;    // the input texture

varying vec3 v_Position;
varying vec2 v_TexCoordinate;

void main() {
    // Send the color from the texture straight out
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
