precision mediump float;        // default medium precision
uniform sampler2D u_Texture;    // the input texture
varying vec2 v_TexCoordinate;   // interpolated texture coordinate per fragment
uniform vec4 u_Color;
uniform float u_Width;
// The entry point for our fragment shader.
void main() {
    vec4 color;
    float dist = abs(v_TexCoordinate.y - texture2D(u_Texture,
    v_TexCoordinate).r);
    if(dist < u_Width){
        color = u_Color;
    }
    gl_FragColor = color;
}
